// API Configuration
const API_BASE_URL = 'http://localhost:8080';

// Current user state
let currentRole = 'GUEST';
let currentUserCode = '';
let currentUserEmail = '';
let currentProfileCode = ''; // Selected profile code
let currentProfileName = ''; // Selected profile name
let isOwnerProfile = false; // True if current profile is the account owner (first profile)
let loggedInUser = null;
let studiosMap = {};
let moviesMap = {};
let watchHistoryMap = {};
let profilesMap = {};
let currentMovies = [];
let movieRatings = {};

// Toggle between login, register, and forgot password views
function showLogin(event) {
  if (event) event.preventDefault();
  document.getElementById('loginForm').style.display = 'block';
  document.getElementById('registerForm').style.display = 'none';
  document.getElementById('forgotView').style.display = 'none';
  document.getElementById('loginError').textContent = '';
  document.getElementById('loginError').classList.remove('show');
}

function showRegister(event) {
  if (event) event.preventDefault();
  document.getElementById('loginForm').style.display = 'none';
  document.getElementById('registerForm').style.display = 'block';
  document.getElementById('forgotView').style.display = 'none';
  document.getElementById('registerError').textContent = '';
  document.getElementById('registerError').classList.remove('show');
}

function showForgotPassword(event) {
  if (event) event.preventDefault();
  document.getElementById('loginForm').style.display = 'none';
  document.getElementById('registerForm').style.display = 'none';
  document.getElementById('forgotView').style.display = 'block';
  document.getElementById('resetError').classList.remove('show');
}

async function resetPassword(event) {
  event.preventDefault();
  
  const email = document.getElementById('resetEmail').value;
  const newPassword = document.getElementById('resetPassword').value;
  const confirmPassword = document.getElementById('resetConfirm').value;
  const errorDiv = document.getElementById('resetError');
  
  if (!email) {
    errorDiv.textContent = 'Please enter your email address';
    errorDiv.classList.add('show');
    return;
  }
  
  if (!newPassword) {
    errorDiv.textContent = 'Please enter a new password';
    errorDiv.classList.add('show');
    return;
  }
  
  if (newPassword !== confirmPassword) {
    errorDiv.textContent = 'Passwords do not match!';
    errorDiv.classList.add('show');
    return;
  }
  
  if (newPassword.length < 6) {
    errorDiv.textContent = 'Password must be at least 6 characters';
    errorDiv.classList.add('show');
    return;
  }
  
  // Clear any previous errors
  errorDiv.classList.remove('show');
  
  try {
    const resetResponse = await fetch(`${API_BASE_URL}/accounts/reset-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: email,
        newPassword: newPassword
      })
    });
    
    if (!resetResponse.ok) {
      const errorData = await resetResponse.json();
      errorDiv.textContent = errorData.error || 'Email not found!';
      errorDiv.classList.add('show');
      return;
    }
    
    const result = await resetResponse.json();
    
    alert('Password reset successfully! You can now log in with your new password.');
    
    showLogin(null);
    document.getElementById('loginEmail').value = email;
    document.getElementById('forgotView').reset();
    
    showMessage('Password reset successfully! Please log in.', 'success');
    
  } catch (error) {
    console.error('Reset password error:', error);
    errorDiv.textContent = 'Failed to reset password. Please try again. Error: ' + error.message;
    errorDiv.classList.add('show');
    alert('Password reset failed! Error: ' + error.message);
  }
}

// Registration function
async function register(event) {
  event.preventDefault();
  
  const email = document.getElementById('regEmail').value;
  const password = document.getElementById('regPassword').value;
  const confirmPassword = document.getElementById('regConfirm').value;
  const planCode = document.getElementById('regPlan').value;
  const errorDiv = document.getElementById('registerError');
  
  if (password !== confirmPassword) {
      errorDiv.textContent = 'Passwords do not match!';
      errorDiv.classList.add('show');
      return;
  }
  
  if (password.length < 6) {
      errorDiv.textContent = 'Password must be at least 6 characters!';
      errorDiv.classList.add('show');
      return;
  }
  
  try {
      // Create new account
      const newAccount = {
      email: email,
      passwordHash: password, // In production, hash this!
      sub_code: planCode,
      createdDate: new Date().toISOString().split('T')[0], // YYYY-MM-DD
      role: 'User'
      };
      
      const response = await fetch(`${API_BASE_URL}/accounts`, {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
          'X-User-Role': 'GUEST' // Allow guest registration
      },
      body: JSON.stringify(newAccount)
      });
      
      if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Registration failed');
      }
      
      // Registration successful!
      showMessage('Account created successfully! Please log in.', 'success');
      
      // Switch back to login view and pre-fill email
      showLogin(null);
      document.getElementById('loginEmail').value = email;
      
  } catch (error) {
      console.error('Registration error:', error);
      errorDiv.textContent = error.message || 'Registration failed. Email may already exist.';
      errorDiv.classList.add('show');
  }
}

// Login function
async function login(event) {
  event.preventDefault();
  
  const email = document.getElementById('loginEmail').value;
  const password = document.getElementById('loginPassword').value;
  const errorDiv = document.getElementById('loginError');
  
  try {
      // Call the secure login endpoint
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email, password })
      });
      
      if (!response.ok) {
      // Login failed
      errorDiv.textContent = 'Invalid email or password';
      errorDiv.classList.add('show');
      return;
      }
      
      const account = await response.json();
      
      // Login successful!
      loggedInUser = account;
      currentRole = (account.role || 'User').toUpperCase(); // Normalize to uppercase
      currentUserCode = account.account_code;
      currentUserEmail = account.email;
      
      if (currentRole === 'ADMIN') {
        document.getElementById('login-screen').style.display = 'none';
        document.getElementById('main-app').style.display = 'block';
        document.getElementById('welcomeMessage').textContent = `Welcome, ${email}`;
        document.getElementById('currentRole').textContent = `Role: ${currentRole}`;
        
        const adminElements = document.querySelectorAll('.admin-only');
        adminElements.forEach(el => el.style.removeProperty('display'));
        
        await loadStudios();
        loadMovies();
      } else {
        document.getElementById('login-screen').style.display = 'none';
        await showProfileSelectionScreen();
      }
      
  } catch (error) {
      errorDiv.textContent = 'Invalid email or password. Please try again.';
      errorDiv.classList.add('show');
  }
}

// Profile Selection Functions
async function showProfileSelectionScreen() {
  try {
      // Fetch profiles for the logged-in account
      const profiles = await fetch(`${API_BASE_URL}/profiles/account/${currentUserCode}`, {
      headers: {
          'X-User-Role': 'ACCOUNT',
          'X-Account-Code': currentUserCode
      }
      }).then(res => res.json());
      
      // Sort profiles by profileID to identify owner (first profile created)
      profiles.sort((a, b) => a.profileID - b.profileID);
      const ownerProfileID = profiles.length > 0 ? profiles[0].profileID : null;
      
      // Display profile selection screen
      document.getElementById('profile-screen').style.display = 'flex';
      
      // Render profile cards
      const profileGrid = document.getElementById('profile-grid');
      profileGrid.innerHTML = profiles.map(profile => {
      const isOwner = profile.profileID === ownerProfileID;
      return `
          <div class="profile-card" onclick="selectProfile('${profile.profile_code}', '${profile.ProfileName}', ${isOwner})">
        <div class="profile-avatar">${profile.ProfileName.charAt(0).toUpperCase()}</div>
        <div class="profile-name ${isOwner ? 'owner-profile' : ''}">${profile.ProfileName}</div>
          </div>
      `;
      }).join('');
      
      if (profiles.length === 0) {
      profileGrid.innerHTML = '<p style="color: #999; grid-column: 1/-1;">No profiles found. Create one to get started!</p>';
      }
      
  } catch (error) {
      console.error('Error loading profiles:', error);
      showMessage('Failed to load profiles', 'error');
  }
}

function selectProfile(profileCode, profileName, isOwner = false) {
  currentProfileCode = profileCode;
  currentProfileName = profileName;
  isOwnerProfile = isOwner;
  
  document.getElementById('profile-screen').style.display = 'none';
  document.getElementById('main-app').style.display = 'block';
  
  const welcomeText = isOwner ? `Welcome, <span class="owner-name">${profileName}</span>` : `Welcome, ${profileName}`;
  document.getElementById('welcomeMessage').innerHTML = welcomeText;
  
  const roleBadge = document.getElementById('roleBadge');
  if (roleBadge) {
    roleBadge.textContent = `[${currentRole}]`;
    roleBadge.className = currentRole === 'ADMIN' ? 'role-badge admin-badge' : 'role-badge user-badge';
  }
  
  document.querySelectorAll('.admin-only').forEach(el => {
      el.style.display = 'none';
  });
  
  loadStudios().then(() => {
      loadMovies();
  });
}

function showAddProfileForm() {
  document.getElementById('add-profile-form').style.display = 'block';
  document.getElementById('newProfileName').focus();
}

function hideAddProfileForm() {
  document.getElementById('add-profile-form').style.display = 'none';
  document.getElementById('newProfileName').value = '';
  document.getElementById('addProfileError').classList.remove('show');
}

async function createNewProfile(event) {
  event.preventDefault();
  
  const profileName = document.getElementById('newProfileName').value.trim();
  const errorDiv = document.getElementById('addProfileError');
  
  try {
      const newProfile = {
      account_code: currentUserCode,
      ProfileName: profileName
      };
      
      const response = await fetch(`${API_BASE_URL}/profiles`, {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
          'X-User-Role': 'ACCOUNT',
          'X-Account-Code': currentUserCode
      },
      body: JSON.stringify(newProfile)
      });
      
      if (!response.ok) {
      throw new Error('Failed to create profile');
      }
      
      hideAddProfileForm();
      await showProfileSelectionScreen();
      
  } catch (error) {
      console.error('Error creating profile:', error);
      errorDiv.textContent = error.message || 'Failed to create profile. You may have reached your plan limit.';
      errorDiv.classList.add('show');
  }
}

function backToLoginFromProfiles() {
  loggedInUser = null;
  currentRole = 'GUEST';
  currentUserCode = '';
  currentUserEmail = '';
  currentProfileCode = '';
  currentProfileName = '';
  isOwnerProfile = false;
  
  document.getElementById('profile-screen').style.display = 'none';
  document.getElementById('login-screen').style.display = 'flex';
  
  showMessage('Returned to login', 'success');
}

// Logout function
function logout() {
  loggedInUser = null;
  currentRole = 'GUEST';
  currentUserCode = '';
  currentUserEmail = '';
  currentProfileCode = '';
  currentProfileName = '';
  isOwnerProfile = false;
  
  document.getElementById('login-screen').style.display = 'flex';
  document.getElementById('main-app').style.display = 'none';
  
  document.getElementById('loginForm').reset();
  document.getElementById('loginError').classList.remove('show');
  
  showMessage('Logged out successfully', 'success');
}

// Show/hide tabs
function showTab(tabName) {
  document.querySelectorAll('.tab-content').forEach(tab => {
      tab.classList.remove('active');
  });
  
  document.querySelectorAll('.tab-btn').forEach(btn => {
      btn.classList.remove('active');
  });
  
  document.getElementById(`${tabName}-tab`).classList.add('active');
  event.target.classList.add('active');
}

// Show status message
function showMessage(message, type = 'success') {
  const statusDiv = document.getElementById('statusMessage');
  statusDiv.textContent = message;
  statusDiv.className = `status-message ${type} show`;
  
  setTimeout(() => {
      statusDiv.classList.remove('show');
  }, 3000);
}

// Make API request with authentication headers
async function apiRequest(endpoint, options = {}) {
  const headers = {
      'Content-Type': 'application/json',
      ...options.headers
  };
  
  if (currentRole !== 'GUEST') {
      headers['X-User-Role'] = currentRole;
      
      if (currentRole === 'ACCOUNT' && currentUserCode) {
      headers['X-Account-Code'] = currentUserCode;
      }
      
      // If a profile is selected, include profile code
      if (currentProfileCode) {
      headers['X-Profile-Code'] = currentProfileCode;
      }
  }
  
  try {
      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers
      });
      
      if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || `HTTP ${response.status}`);
      }
      
      return await response.json();
  } catch (error) {
      showMessage(`Error: ${error.message}`, 'error');
      throw error;
  }
}

// Load Watch History for current profile
async function loadWatchHistory() {
  try {
      // Only load if a profile is selected (not for admin)
      if (!currentProfileCode) {
      watchHistoryMap = {};
      return;
      }
      
      const watchHistory = await apiRequest('/watch-history');
      
      // Filter for current profile and create map: movie_code -> {progress, watchDate}
      watchHistoryMap = {};
      watchHistory
      .filter(wh => wh.profile_code === currentProfileCode)
      .forEach(wh => {
          watchHistoryMap[wh.movie_code] = {
        progress: wh.Progress || 0,
        watchDate: wh.watchDate
          };
      });
      
  } catch (error) {
      console.error('Failed to load watch history:', error);
      watchHistoryMap = {};
  }
}

// Load Studios and create lookup map
async function loadStudios() {
  try {
      const studios = await apiRequest('/studios');
      // Create a map: studioCode -> studioName
      studiosMap = {};
      studios.forEach(studio => {
      // Handle both studioCode and studio_code property names
      const code = studio.studioCode || studio.studio_code;
      const name = studio.studioName || studio.studio_name;
      if (code && name) {
          studiosMap[code] = name;
      }
      });
  } catch (error) {
      console.error('Failed to load studios:', error);
  }
}

async function loadProfiles() {
  try {
      const profiles = await apiRequest('/profiles');
      // Create a map: profileCode -> profileName
      profilesMap = {};
      profiles.forEach(profile => {
      const code = profile.profile_code;
      const name = profile.ProfileName;
      if (code && name) {
          profilesMap[code] = name;
      }
      });
  } catch (error) {
      console.error('Failed to load profiles:', error);
  }
}

// Load Movies
async function loadMovies() {
  const listDiv = document.getElementById('movies-list');
  listDiv.innerHTML = '<div class="loading">Loading movies...</div>';
  
  try {
      if (Object.keys(studiosMap).length === 0) {
      await loadStudios();
      }
      
      await loadWatchHistory();
      
      const movies = await apiRequest('/movies');
      currentMovies = movies;
      
      await loadMovieRatings();
      displayMovies(movies);
  } catch (error) {
      console.error('Load movies error:', error);
      listDiv.innerHTML = '<p>Failed to load movies</p>';
  }
}

async function loadMovieRatings() {
  try {
    const ratings = await apiRequest('/ratings');
    movieRatings = {};
    ratings.forEach(rating => {
      const movieCode = rating.movie_code;
      if (!movieRatings[movieCode]) {
        movieRatings[movieCode] = { total: 0, count: 0 };
      }
      movieRatings[movieCode].total += rating.ratingValue;
      movieRatings[movieCode].count += 1;
    });
  } catch (error) {
    console.error('Failed to load ratings for sorting:', error);
  }
}

function sortMovies() {
  const sortType = document.getElementById('movieSortType').value;
  const sorted = [...currentMovies];
  
  switch(sortType) {
    case 'title':
      sorted.sort((a, b) => a.title.localeCompare(b.title));
      break;
    case 'year':
      sorted.sort((a, b) => b.year - a.year);
      break;
    case 'length':
      sorted.sort((a, b) => a.length - b.length);
      break;
    case 'rating':
      sorted.sort((a, b) => {
        const movieCodeA = a.movieCode || a.movie_code;
        const movieCodeB = b.movieCode || b.movie_code;
        const avgA = movieRatings[movieCodeA] ? movieRatings[movieCodeA].total / movieRatings[movieCodeA].count : 0;
        const avgB = movieRatings[movieCodeB] ? movieRatings[movieCodeB].total / movieRatings[movieCodeB].count : 0;
        return avgB - avgA;
      });
      break;
  }
  
  displayMovies(sorted);
}

// Display movies
function displayMovies(movies) {
  const listDiv = document.getElementById('movies-list');
  
  if (movies.length === 0) {
      listDiv.innerHTML = '<p>No movies found</p>';
      return;
  }
  
  listDiv.innerHTML = movies.map(movie => {
      // Handle both studioCode and studio_code property names
      const studioCodeValue = movie.studioCode || movie.studio_code;
      const movieCodeValue = movie.movieCode || movie.movie_code;
      const studioName = studiosMap[studioCodeValue] || studioCodeValue || 'Unknown Studio';
      
      // Check if this movie has been watched
      const watchData = watchHistoryMap[movieCodeValue];
      const hasWatched = !!watchData;
      const progress = hasWatched ? watchData.progress : 0;
      const progressPercent = movie.length > 0 ? Math.min(100, (progress / movie.length) * 100) : 0;
      
      return `
      <div class="item-card ${hasWatched ? 'watched' : ''}">
          <h3>${movie.title}</h3>
          <p><strong>Year:</strong> ${movie.year}</p>
          <p><strong>Length:</strong> ${movie.length} minutes</p>
          <p><strong>Studio:</strong> ${studioName}</p>
          ${hasWatched ? `
        <div class="watch-progress">
            <div class="progress-bar-container">
        <div class="progress-bar" style="width: ${progressPercent}%"></div>
            </div>
            <p class="progress-text">${progress}/${movie.length} min (${progressPercent.toFixed(0)}%)</p>
            <p class="watch-date">Watched: ${watchData.watchDate}</p>
        </div>
          ` : ''}
          ${currentRole === 'ADMIN' ? `
        <div class="admin-actions">
            <button onclick="editMovie(${movie.movieID}, '${movie.title.replace(/'/g, "\\'")}', ${movie.year}, '${studioCodeValue}', ${movie.length})" class="btn-edit">Edit</button>
            <button onclick="deleteMovie(${movie.movieID}, '${movie.title.replace(/'/g, "\\'")}', '${movieCodeValue}')" class="btn-delete">Delete</button>
        </div>
          ` : ''}
      </div>
      `;
  }).join('');
}

// Search movies
async function searchMovies() {
  const title = document.getElementById('searchTitle').value;
  if (!title) {
      showMessage('Please enter a search term', 'error');
      return;
  }
  
  const listDiv = document.getElementById('movies-list');
  listDiv.innerHTML = '<div class="loading">Searching...</div>';
  
  try {
      const movies = await apiRequest(`/movies/search/title?title=${encodeURIComponent(title)}`);
      displayMovies(movies);
  } catch (error) {
      listDiv.innerHTML = '<p>Search failed</p>';
  }
}

// Load Actors
async function loadActors() {
  const listDiv = document.getElementById('actors-list');
  listDiv.innerHTML = '<div class="loading">Loading actors...</div>';
  
  try {
      const actors = await apiRequest('/actors');
      displayActors(actors);
  } catch (error) {
      listDiv.innerHTML = '<p>Failed to load actors</p>';
  }
}

// Get movies for an actor from movie_actor table
async function getMoviesForActor(actorCode) {
  try {
      // Try to get all movies and filter by actor
      // You'll need a backend endpoint like /movie-actor/actor/{actorCode}
      // For now, we'll use a workaround
      const response = await fetch(`${API_BASE_URL}/actors/search/movie/`);
      // This is a placeholder - implement proper endpoint later
      return [];
  } catch (error) {
      return [];
  }
}

// Display actors with their movies
async function displayActors(actors) {
  const listDiv = document.getElementById('actors-list');
  
  if (actors.length === 0) {
      listDiv.innerHTML = '<p>No actors found</p>';
      return;
  }
  
  listDiv.innerHTML = actors.map(actor => `
      <div class="item-card" id="actor-card-${actor.actorId}">
      <h3>${actor.FirstName} ${actor.lastName}</h3>
      <p><strong>Birth Date:</strong> ${actor.BirthDate}</p>
      <p class="actor-movies"><strong>Movies:</strong> <span class="loading-inline">Loading...</span></p>
      </div>
  `).join('');
  
  actors.forEach(async (actor) => {
      try {
      // Fetch movies this actor appeared in
      const actorMovies = await apiRequest(`/actors/${actor.actorId}/movies`).catch(() => []);
      
      const moviesList = document.querySelector(`#actor-card-${actor.actorId} .actor-movies span`);
      
      if (actorMovies && actorMovies.length > 0) {
          moviesList.innerHTML = actorMovies.map(m => m.title).join(', ');
      } else {
          moviesList.innerHTML = '<em>No movies found</em>';
      }
      } catch (error) {
      // If endpoint doesn't exist, show placeholder
      const moviesList = document.querySelector(`#actor-card-${actor.actorId} .actor-movies span`);
      if (moviesList) {
          moviesList.innerHTML = '<em>Movies data unavailable</em>';
      }
      }
  });
}

// Update search placeholder based on selected type
function updateActorSearchPlaceholder() {
  const searchType = document.getElementById('actorSearchType').value;
  const input = document.getElementById('actorSearchInput');
  
  const placeholders = {
      'firstName': 'Enter first name...',
      'lastName': 'Enter last name...',
      'movie': 'Enter movie title...',
      'birthYear': 'Enter birth year (e.g., 1990)...'
  };
  
  input.placeholder = placeholders[searchType];
  input.value = '';
}

// Search actors with different criteria
async function searchActors() {
  const searchType = document.getElementById('actorSearchType').value;
  const searchValue = document.getElementById('actorSearchInput').value;
  
  if (!searchValue) {
      showMessage('Please enter a search term', 'error');
      return;
  }
  
  const listDiv = document.getElementById('actors-list');
  listDiv.innerHTML = '<div class="loading">Searching...</div>';
  
  try {
      let actors = [];
      
      switch(searchType) {
      case 'firstName':
          // Search by first name (filter client-side)
          const allActors = await apiRequest('/actors');
          actors = allActors.filter(actor => 
        actor.FirstName.toLowerCase().includes(searchValue.toLowerCase())
          );
          break;
          
      case 'lastName':
          // Search by last name (backend endpoint)
          actors = await apiRequest(`/actors/search/lastname?lastName=${encodeURIComponent(searchValue)}`);
          break;
          
      case 'movie':
          // Search by movie title (backend endpoint)
          actors = await apiRequest(`/actors/search/movie/${encodeURIComponent(searchValue)}`);
          break;
          
      case 'birthYear':
          // Search by birth year (filter client-side)
          const allActorsByYear = await apiRequest('/actors');
          actors = allActorsByYear.filter(actor => {
        const birthYear = new Date(actor.BirthDate).getFullYear();
        return birthYear.toString() === searchValue;
          });
          break;
      }
      
      displayActors(actors);
      
      if (actors.length === 0) {
      showMessage(`No actors found for ${searchType}: "${searchValue}"`, 'error');
      }
      
  } catch (error) {
      listDiv.innerHTML = '<p>Search failed</p>';
  }
}

// Load Ratings
async function loadRatings() {
  const listDiv = document.getElementById('ratings-list');
  listDiv.innerHTML = '<div class="loading">Loading ratings...</div>';
  
  try {
      // Load movies and profiles if not already loaded
      if (Object.keys(moviesMap).length === 0) {
      await loadMoviesForRatings();
      }
      if (Object.keys(profilesMap).length === 0) {
      await loadProfiles();
      }
      
      const ratings = await apiRequest('/ratings');
      displayRatings(ratings);
  } catch (error) {
      listDiv.innerHTML = '<p>Failed to load ratings</p>';
  }
}

// Load movies and create map for ratings display
async function loadMoviesForRatings() {
  try {
      const movies = await apiRequest('/movies');
      moviesMap = {};
      movies.forEach(movie => {
      moviesMap[movie.movieCode] = movie.title;
      });
  } catch (error) {
      console.error('Failed to load movies for ratings:', error);
  }
}

// Display ratings
function displayRatings(ratings) {
  const listDiv = document.getElementById('ratings-list');
  
  if (ratings.length === 0) {
      listDiv.innerHTML = '<p>No ratings found</p>';
      return;
  }
  
  console.log('Current profile code:', currentProfileCode);
  console.log('Total ratings:', ratings.length);
  
  listDiv.innerHTML = ratings.map(rating => {
      const movieTitle = moviesMap[rating.movie_code] || rating.movie_code;
      const profileName = profilesMap[rating.profile_code] || rating.profile_code;
      const isMyRating = rating.profile_code === currentProfileCode;
      
      return `
      <div class="item-card ${isMyRating ? 'my-rating' : ''}">
          <div class="rating-header">
        <h3><span class="icon-movie">&#127916;</span> ${movieTitle}</h3>
        ${isMyRating ? `
            <div class="rating-actions">
          <button class="btn-edit" onclick="editRating('${rating.movie_code}', ${rating.ratingValue}, '${(rating.review || '').replace(/'/g, "\\'")}')">Edit</button>
          <button class="btn-delete" onclick="deleteRating('${rating.profile_code}', '${rating.movie_code}', '${movieTitle}')">Delete</button>
            </div>
        ` : ''}
          </div>
          <p class="rating"><span class="icon-star">&#9733;</span> ${rating.ratingValue}/5</p>
          <p><strong>Profile:</strong> ${profileName}${isMyRating ? ' (You)' : ''}</p>
          <p><strong>Date:</strong> ${rating.ratingDate}</p>
          ${rating.review ? `<p class="review-text"><strong>Review:</strong> "${rating.review}"</p>` : '<p class="no-review"><em>No review provided</em></p>'}
      </div>
      `;
  }).join('');
}

// Delete Rating Function
async function deleteRating(profileCode, movieCode, movieTitle) {
  if (!confirm(`Delete your rating for "${movieTitle}"?`)) {
    return;
  }
  
  try {
    await apiRequest(`/ratings/${profileCode}/${movieCode}`, {
      method: 'DELETE'
    });
    
    showMessage('Rating deleted successfully!', 'success');
    loadRatings();
  } catch (error) {
    showMessage('Failed to delete rating', 'error');
  }
}

// Add Rating Function
async function showAddRatingModal() {
  console.log('Opening add rating modal...');
  console.log('Current profile code:', currentProfileCode);
  
  try {
    document.getElementById('addRatingModal').classList.add('show');
    
    if (Object.keys(moviesMap).length === 0) {
        await loadMoviesForRatings();
    }
    
    const ratings = await apiRequest('/ratings');
    console.log('All ratings received:', ratings.length);
    console.log('Sample rating:', ratings[0]);
    
    const myRatings = ratings.filter(r => r.profile_code === currentProfileCode);
    const ratedMovieCodes = new Set(myRatings.map(r => r.movie_code));
    const movieSelect = document.getElementById('addRatingMovieSelect');
    const movies = await apiRequest('/movies');
    
    const unratedMovies = movies.filter(m => {
      const movieCode = m.movieCode || m.movie_code;
      return !ratedMovieCodes.has(movieCode);
    });
    
    movieSelect.innerHTML = '<option value="">Choose a movie...</option>' + 
        unratedMovies.map(movie => {
            const movieCode = movie.movieCode || movie.movie_code;
            const movieTitle = movie.title;
            return `<option value="${movieCode}">${movieTitle}</option>`;
        })
        .join('');
    
    if (unratedMovies.length === 0) {
        movieSelect.innerHTML = '<option value="">You have rated all movies!</option>';
        document.getElementById('addRatingError').textContent = 'You have already rated all available movies.';
        document.getElementById('addRatingError').classList.add('show');
    } else {
        document.getElementById('addRatingError').classList.remove('show');
    }
  } catch (error) {
    console.error('Error opening add rating modal:', error);
    showMessage('Failed to load rating form. Please try again.', 'error');
  }
}

function closeAddRatingModal() {
  document.getElementById('addRatingModal').classList.remove('show');
  document.getElementById('addRatingForm').reset();
  document.getElementById('addRatingError').classList.remove('show');
}

async function submitNewRating(event) {
  event.preventDefault();
  
  const movieCode = document.getElementById('addRatingMovieSelect').value;
  const ratingValue = parseInt(document.getElementById('addRatingValue').value);
  const review = document.getElementById('addReviewText').value.trim();
  const errorDiv = document.getElementById('addRatingError');
  
  if (!movieCode) {
      errorDiv.textContent = 'Please select a movie';
      errorDiv.classList.add('show');
      return;
  }
  
  if (ratingValue < 1 || ratingValue > 5) {
      errorDiv.textContent = 'Rating must be between 1 and 5';
      errorDiv.classList.add('show');
      return;
  }
  
  try {
      const newRating = {
      profile_code: currentProfileCode,
      movie_code: movieCode,
      ratingValue: ratingValue,
      review: review || null,
      ratingDate: new Date().toISOString().split('T')[0]
      };
      
      console.log('Submitting rating:', newRating);
      console.log('Headers:', { role: currentRole, profileCode: currentProfileCode });
      
      const response = await fetch(`${API_BASE_URL}/ratings`, {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
          'X-User-Role': currentRole,
          'X-Profile-Code': currentProfileCode
      },
      body: JSON.stringify(newRating)
      });
      
      console.log('Response status:', response.status);
      
      if (!response.ok) {
      const errorText = await response.text();
      console.error('Server error:', errorText);
      throw new Error(`Failed to add rating: ${response.status} - ${errorText}`);
      }
      
      closeAddRatingModal();
      await loadRatings();
      
  } catch (error) {
      console.error('Add rating error:', error);
      errorDiv.textContent = 'Failed to add rating. You may have already rated this movie.';
      errorDiv.classList.add('show');
  }
}

// Edit Rating Function
let currentEditingRating = null;

function editRating(movieCode, currentRatingValue, currentReview) {
  currentEditingRating = { movieCode, currentRatingValue, currentReview };
  
  document.getElementById('editRatingModal').classList.add('show');
  
  document.getElementById('editRatingValue').value = currentRatingValue;
  document.getElementById('editReviewText').value = currentReview || '';
  document.getElementById('editMovieTitle').textContent = moviesMap[movieCode] || movieCode;
}

function closeEditRatingModal() {
  document.getElementById('editRatingModal').classList.remove('show');
  currentEditingRating = null;
  document.getElementById('editRatingError').classList.remove('show');
}

async function submitEditRating(event) {
  event.preventDefault();
  
  const newRatingValue = parseInt(document.getElementById('editRatingValue').value);
  const newReview = document.getElementById('editReviewText').value.trim();
  const errorDiv = document.getElementById('editRatingError');
  
  if (newRatingValue < 1 || newRatingValue > 5) {
      errorDiv.textContent = 'Rating must be between 1 and 5';
      errorDiv.classList.add('show');
      return;
  }
  
  try {
      const updateData = {
      ratingValue: newRatingValue,
      review: newReview || null,
      ratingDate: new Date().toISOString().split('T')[0]
      };
      
      const url = `${API_BASE_URL}/ratings/profile/${currentProfileCode}/movie/${currentEditingRating.movieCode}`;
      console.log('Updating rating at:', url);
      
      const response = await fetch(url, {
      method: 'PUT',
      headers: {
          'Content-Type': 'application/json',
          'X-User-Role': currentRole,
          'X-Profile-Code': currentProfileCode
      },
      body: JSON.stringify(updateData)
      });
      
      if (!response.ok) {
      throw new Error('Failed to update rating');
      }
      
      showMessage('Rating updated successfully!', 'success');
      closeEditRatingModal();
      
      // Reload ratings
      await loadRatings();
      
  } catch (error) {
      console.error('Update rating error:', error);
      errorDiv.textContent = 'Failed to update rating. Please try again.';
      errorDiv.classList.add('show');
  }
}

// Temporary storage for pending movie data
let pendingMovieData = null;

// Find studio by name
async function findStudioByName(studioName) {
  try {
      const studios = await apiRequest('/studios');
      return studios.find(s => s.studioName.toLowerCase() === studioName.toLowerCase());
  } catch (error) {
      return null;
  }
}

// Show studio modal
function showStudioModal(studioName) {
  document.getElementById('studioNameDisplay').textContent = studioName;
  document.getElementById('newStudioName').value = studioName;
  document.getElementById('studioModal').classList.add('show');
}

// Close studio modal
function closeStudioModal() {
  document.getElementById('studioModal').classList.remove('show');
  document.getElementById('addStudioModalForm').reset();
  pendingMovieData = null;
}

// Add Movie Form Handler with Studio Validation
document.getElementById('addMovieForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  console.log('=== ADD MOVIE FORM SUBMITTED ===');  
  const title = document.getElementById('movieTitle').value;
  const year = document.getElementById('movieYear').value;
  const length = document.getElementById('movieLength').value;
  const studioName = document.getElementById('studioName').value;
  
  const studio = await findStudioByName(studioName);
  
  if (!studio) {
      pendingMovieData = {
        title: title,
        year: parseInt(year),
        studioName: studioName,
        length: parseInt(length)
      };
      showStudioModal(studioName);
      return;
  }
  
  await addMovieWithStudio(studio.studioCode);
});

async function addMovieWithStudio(studioCode) {
  const movieData = pendingMovieData || {
    title: document.getElementById('movieTitle').value,
    year: parseInt(document.getElementById('movieYear').value),
    length: parseInt(document.getElementById('movieLength').value)
  };
  
  movieData.studioCode = studioCode;
  delete movieData.studioName;
  
  try {
      const newMovie = await apiRequest('/movies', {
      method: 'POST',
      body: JSON.stringify(movieData)
      });
      
      console.log('Movie added successfully:', newMovie);
      document.getElementById('addMovieForm').reset();
      pendingMovieData = null;
      await loadStudios();
      loadMovies();
  } catch (error) {
      console.error('Failed to add movie:', error);
      showMessage('Failed to add movie: ' + error.message, 'error');
  }
}

// Edit Movie - Show modal with current values
function editMovie(movieID, title, year, studioCode, length) {
  document.getElementById('editMovieID').value = movieID;
  document.getElementById('editMovieTitle').value = title;
  document.getElementById('editMovieYear').value = year;
  document.getElementById('editMovieLength').value = length;
  document.getElementById('editMovieStudio').value = studiosMap[studioCode] || studioCode;
  document.getElementById('editMovieError').classList.remove('show');
  
  document.getElementById('editMovieModal').style.display = 'block';
}

// Close Edit Movie Modal
function closeEditMovieModal() {
  document.getElementById('editMovieModal').style.display = 'none';
  document.getElementById('editMovieForm').reset();
  document.getElementById('editMovieError').classList.remove('show');
}

// Submit Edit Movie Form
async function submitEditMovie(event) {
  event.preventDefault();
  
  const movieID = document.getElementById('editMovieID').value;
  const newTitle = document.getElementById('editMovieTitle').value;
  const newYear = parseInt(document.getElementById('editMovieYear').value);
  const newLength = parseInt(document.getElementById('editMovieLength').value);
  const newStudioName = document.getElementById('editMovieStudio').value;
  const errorDiv = document.getElementById('editMovieError');
  
  // Find studio by name
  const studio = await findStudioByName(newStudioName);
  if (!studio) {
    errorDiv.textContent = `Studio "${newStudioName}" not found. Please add it first in the Studios tab.`;
    errorDiv.classList.add('show');
    return;
  }
  
  try {
    const updatedMovie = await apiRequest(`/movies/${movieID}`, {
      method: 'PUT',
      body: JSON.stringify({
        title: newTitle,
        year: newYear,
        length: newLength,
        studioCode: studio.studioCode
      })
    });
    
    showMessage(`Movie "${updatedMovie.title}" updated successfully!`, 'success');
    closeEditMovieModal();
    loadMovies();
  } catch (error) {
    errorDiv.textContent = 'Failed to update movie. Please try again.';
    errorDiv.classList.add('show');
  }
}

// Delete Movie
async function deleteMovie(movieID, title, movieCode) {
  if (!confirm(`Are you sure you want to delete "${title}"?\n\nThis will also delete:\n- All ratings for this movie\n- All watch history for this movie`)) {
    return;
  }
  
  try {
    await apiRequest(`/movies/${movieID}`, {
      method: 'DELETE'
    });
    
    showMessage(`Movie "${title}" deleted successfully!`, 'success');
    
    // Remove from local maps
    delete watchHistoryMap[movieCode];
    
    loadMovies();
  } catch (error) {
    showMessage('Failed to delete movie', 'error');
  }
}

// Add Studio from Modal
document.getElementById('addStudioModalForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const studioData = {
      studioName: document.getElementById('newStudioName').value,
      address: document.getElementById('newStudioAddress').value
  };
  
  try {
      const newStudio = await apiRequest('/studios', {
      method: 'POST',
      body: JSON.stringify(studioData)
      });
      
      closeStudioModal();
      
      // Now add the movie with the new studio
      if (pendingMovieData) {
      await addMovieWithStudio(newStudio.studioCode);
      }
  } catch (error) {
      showMessage('Failed to add studio', 'error');
  }
});

// Add Actor Form Handler
document.getElementById('addActorForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const actorData = {
      FirstName: document.getElementById('actorFirstName').value,
      lastName: document.getElementById('actorLastName').value,
      BirthDate: document.getElementById('actorBirthDate').value
  };
  
  const moviesInput = document.getElementById('actorMovies').value.trim();
  
  try {
      const newActor = await apiRequest('/actors', {
        method: 'POST',
        body: JSON.stringify(actorData)
      });
      
      // Process movies if provided
      if (moviesInput && newActor.actorCode) {
      const movieIdentifiers = moviesInput.split(',').map(m => m.trim()).filter(m => m);
      console.log('Movie identifiers:', movieIdentifiers);
      
      const allMovies = await apiRequest('/movies');
      
      for (const identifier of movieIdentifiers) {
          // Find movie by title or code (case-insensitive)
          const movie = allMovies.find(m => 
          m.title.toLowerCase() === identifier.toLowerCase() || 
          (m.movieCode && m.movieCode.toUpperCase() === identifier.toUpperCase()) ||
          (m.movie_code && m.movie_code.toUpperCase() === identifier.toUpperCase())
          );
          
          if (movie) {
          const movieCode = movie.movieCode || movie.movie_code;
          try {
              await apiRequest('/movie-actor', {
              method: 'POST',
              body: JSON.stringify({
                  movie_code: movieCode,
                  actor_code: newActor.actorCode
              })
              });
              console.log(`✓ Added actor to "${movie.title}"`);
          } catch (error) {
              console.error(`✗ Failed to add actor to "${movie.title}":`, error);
          }
          } else {
          console.warn(`Movie "${identifier}" not found - skipping`);
          }
      }
      }
      
      document.getElementById('addActorForm').reset();
      loadActors();
  } catch (error) {
      console.error('Failed to add actor:', error);
  }
});

// Open Settings 
async function settings() {
  document.getElementById('settingsModal').classList.add('show');
  
  if (currentRole === 'ADMIN' || currentRole.toUpperCase() === 'ADMIN') {
      console.log('Admin settings access granted');
      // Show all tabs for admin
      document.querySelectorAll('.settings-tab').forEach(tab => tab.style.display = '');
      
      document.getElementById('settingsEmail').textContent = currentUserEmail || 'N/A';
      document.getElementById('settingsAccountCode').textContent = currentUserCode || 'N/A';
      document.getElementById('settingsCreatedDate').textContent = loggedInUser?.createdDate || 'N/A';
      
      await loadCurrentSubscription();
      await loadAvailablePlans();
      
      showSettingsTab('account');
      return;
  }
  
  if (isOwnerProfile) {
      // Owner profile: Show all tabs
      document.querySelectorAll('.settings-tab').forEach(tab => tab.style.display = '');
      
      document.getElementById('settingsEmail').textContent = currentUserEmail || 'N/A';
      document.getElementById('settingsAccountCode').textContent = currentUserCode || 'N/A';
      document.getElementById('settingsCreatedDate').textContent = loggedInUser?.createdDate || 'N/A';
      
      await loadCurrentSubscription();
      await loadAvailablePlans();
      await loadProfilesInSettings();
      
      showSettingsTab('account');
  } else {
      // Non-owner profile: Hide Account and Subscription tabs, only show Profiles
      const tabs = document.querySelectorAll('.settings-tab');
      tabs[0].style.display = 'none'; // Account tab
      tabs[1].style.display = 'none'; // Subscription tab
      tabs[2].style.display = ''; // Profiles tab
      
      // Add restriction message to Account and Subscription tabs
      const accountTab = document.getElementById('settings-account');
      const subscriptionTab = document.getElementById('settings-subscription');
      
      accountTab.innerHTML = `
      <div style="text-align: center; padding: 50px;">
          <h3 style="color: #999;"><span class="icon-lock">&#128274;</span> Restricted Access</h3>
          <p style="color: #666; margin-top: 20px;">Only the account owner (first profile) can change password and email settings.</p>
          <p style="color: #999; margin-top: 10px;">Please ask the account owner to make these changes.</p>
      </div>
      `;
      
      subscriptionTab.innerHTML = `
      <div style="text-align: center; padding: 50px;">
          <h3 style="color: #999;"><span class="icon-lock">&#128274;</span> Restricted Access</h3>
          <p style="color: #666; margin-top: 20px;">Only the account owner (first profile) can change subscription plans.</p>
          <p style="color: #999; margin-top: 10px;">Please ask the account owner to make these changes.</p>
      </div>
      `;
      
      await loadProfilesInSettings();
      showSettingsTab('profiles'); // Default to profiles tab for non-owners
  }
}

// Close Settings Modal
function closeSettings() {
  document.getElementById('settingsModal').classList.remove('show');
  
  document.getElementById('changePasswordForm').reset();
  document.getElementById('passwordError').classList.remove('show');
  hideAddProfileInSettings();
  
  document.querySelectorAll('.settings-tab').forEach(tab => tab.style.display = '');
}

// Switch Settings Tabs
function showSettingsTab(tabName) {
  document.querySelectorAll('.settings-tab-content').forEach(content => {
      content.classList.remove('active');
  });
  
  document.querySelectorAll('.settings-tab').forEach(btn => {
      btn.classList.remove('active');
  });
  
  document.getElementById(`settings-${tabName}`).classList.add('active');
  event.target.classList.add('active');
}

// Change Password
async function changePassword(event) {
  event.preventDefault();
  
  const currentPassword = document.getElementById('currentPassword').value;
  const newPassword = document.getElementById('newPassword').value;
  const confirmPassword = document.getElementById('confirmPassword').value;
  const errorDiv = document.getElementById('passwordError');
  
  if (newPassword !== confirmPassword) {
      errorDiv.textContent = 'New passwords do not match!';
      errorDiv.classList.add('show');
      return;
  }
  
  
  
  try {
      // Verify current password first
      const loginCheck = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
          email: currentUserEmail, 
          password: currentPassword 
      })
      });
      
      if (!loginCheck.ok) {
      errorDiv.textContent = 'Current password is incorrect!';
      errorDiv.classList.add('show');
      return;
      }
      
      // Update password
      const updateData = { passwordHash: newPassword };
      
      await fetch(`${API_BASE_URL}/accounts/${loggedInUser.accountID}`, {
      method: 'PUT',
      headers: {
          'Content-Type': 'application/json',
          'X-User-Role': currentRole,
          'X-Account-Code': currentUserCode
      },
      body: JSON.stringify(updateData)
      });
      
      showMessage('Password updated successfully!', 'success');
      document.getElementById('changePasswordForm').reset();
      errorDiv.classList.remove('show');
      
  } catch (error) {
      errorDiv.textContent = 'Failed to update password. Please try again.';
      errorDiv.classList.add('show');
  }
}

// Load Current Subscription
async function loadCurrentSubscription() {
  try {
      const plans = await apiRequest('/subscription-plans');
      const currentPlan = plans.find(p => p.sub_code === loggedInUser.sub_code);
      
      if (currentPlan) {
      document.getElementById('currentPlanName').textContent = currentPlan.planName;
      document.getElementById('currentPlanPrice').textContent = `$${currentPlan.monthlyPrice}/month`;
      document.getElementById('currentPlanDetails').textContent = `Up to ${currentPlan.MaxUsers} profile(s)`;
      }
  } catch (error) {
      console.error('Failed to load current subscription:', error);
  }
}

// Load Available Plans
async function loadAvailablePlans() {
  try {
      const plans = await apiRequest('/subscription-plans');
      const plansGrid = document.getElementById('availablePlans');
      
      plansGrid.innerHTML = plans.map(plan => {
      const isCurrent = plan.sub_code === loggedInUser.sub_code;
      return `
          <div class="plan-card ${isCurrent ? 'current-plan' : ''}" ${!isCurrent ? `onclick="changePlan('${plan.sub_code}', '${plan.planName}')"` : ''}>
        <h4>${plan.planName}</h4>
        <p class="plan-price">$${plan.monthlyPrice}</p>
        <p>${plan.MaxUsers} profile(s)</p>
        ${isCurrent ? '<p style="color: palevioletred; font-weight: bold;"><span class="icon-check">&#10003;</span> Current Plan</p>' : '<button type="button">Select Plan</button>'}
          </div>
      `;
      }).join('');
  } catch (error) {
      console.error('Failed to load plans:', error);
  }
}

// Change Subscription Plan
async function changePlan(planCode, planName) {
  if (!confirm(`Switch to ${planName} plan?`)) {
      return;
  }
  
  try {
      await fetch(`${API_BASE_URL}/accounts/${loggedInUser.accountID}/subscription`, {
      method: 'PUT',
      headers: {
          'Content-Type': 'application/json',
          'X-User-Role': currentRole,
          'X-Account-Code': currentUserCode
      },
      body: JSON.stringify({ sub_code: planCode })
      });
      
      loggedInUser.sub_code = planCode;
      showMessage(`Successfully switched to ${planName}!`, 'success');
      
      // Reload subscription info
      await loadCurrentSubscription();
      await loadAvailablePlans();
      
  } catch (error) {
      showMessage('Failed to change plan. Please try again.', 'error');
  }
}

// Load Profiles in Settings
async function loadProfilesInSettings() {
  try {
      const profiles = await fetch(`${API_BASE_URL}/profiles/account/${currentUserCode}`, {
      headers: {
          'X-User-Role': 'ACCOUNT',
          'X-Account-Code': currentUserCode
      }
      }).then(res => res.json());
      
      const profilesList = document.getElementById('profilesList');
      
      if (profiles.length === 0) {
      profilesList.innerHTML = '<p style="color: #999;">No profiles yet. Create one below!</p>';
      return;
      }
      
      profilesList.innerHTML = profiles.map(profile => `
      <div class="profile-item">
          <div class="profile-item-info">
        <div class="profile-item-avatar">${profile.ProfileName.charAt(0).toUpperCase()}</div>
        <div class="profile-item-name">${profile.ProfileName}</div>
          </div>
          <div class="profile-item-actions">
        <button class="btn-icon delete" onclick="deleteProfile(${profile.profileID}, '${profile.ProfileName}')" title="Delete">
            <span class="icon-trash">&#128465;</span>
        </button>
          </div>
      </div>
      `).join('');
      
  } catch (error) {
      console.error('Failed to load profiles:', error);
  }
}

// Show Add Profile Form in Settings
function showAddProfileInSettings() {
  document.getElementById('addProfileInSettings').style.display = 'block';
  document.getElementById('settingsNewProfileName').focus();
}

// Hide Add Profile Form in Settings
function hideAddProfileInSettings() {
  document.getElementById('addProfileInSettings').style.display = 'none';
  document.getElementById('settingsNewProfileName').value = '';
  document.getElementById('addProfileSettingsError').classList.remove('show');
}

// Add Profile from Settings
async function addProfileInSettings(event) {
  event.preventDefault();
  
  const profileName = document.getElementById('settingsNewProfileName').value.trim();
  const errorDiv = document.getElementById('addProfileSettingsError');
  
  try {
      const newProfile = {
      account_code: currentUserCode,
      ProfileName: profileName
      };
      
      const response = await fetch(`${API_BASE_URL}/profiles`, {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
          'X-User-Role': 'ACCOUNT',
          'X-Account-Code': currentUserCode
      },
      body: JSON.stringify(newProfile)
      });
      
      if (!response.ok) {
      throw new Error('Failed to create profile');
      }
      
      hideAddProfileInSettings();
      await loadProfilesInSettings();
      
  } catch (error) {
      errorDiv.textContent = 'Failed to create profile. You may have reached your plan limit.';
      errorDiv.classList.add('show');
  }
}

// Delete Profile
async function deleteProfile(profileID, profileName) {
  if (!confirm(`Delete profile "${profileName}"? This action cannot be undone.`)) {
      return;
  }
  
  try {
      await fetch(`${API_BASE_URL}/profiles/${profileID}`, {
      method: 'DELETE',
      headers: {
          'X-User-Role': 'ACCOUNT',
          'X-Account-Code': currentUserCode
      }
      });
      
      showMessage(`Profile "${profileName}" deleted successfully!`, 'success');
      await loadProfilesInSettings();
      
  } catch (error) {
      showMessage('Failed to delete profile. Please try again.', 'error');
  }
}

// ==================== ADMIN ACCOUNT MANAGEMENT ====================

// Load all accounts (Admin only)
async function loadAllAccounts() {
  const listDiv = document.getElementById('admin-accounts-list');
  listDiv.innerHTML = '<div class="loading">Loading accounts...</div>';
  
  try {
      const accounts = await apiRequest('/accounts');
      displayAdminAccounts(accounts);
  } catch (error) {
      listDiv.innerHTML = '<p>Failed to load accounts</p>';
      console.error('Load accounts error:', error);
  }
}

// Display accounts for admin
function displayAdminAccounts(accounts) {
  const listDiv = document.getElementById('admin-accounts-list');
  
  if (accounts.length === 0) {
      listDiv.innerHTML = '<p>No accounts found</p>';
      return;
  }
  
  listDiv.innerHTML = accounts.map(account => {
      const isCurrentAdmin = account.email === currentUserEmail;
      return `
      <div class="account-item ${isCurrentAdmin ? 'current-account' : ''}">
          <div class="account-info">
        <h4>${account.email}</h4>
        <p><strong>Account Code:</strong> ${account.account_code}</p>
        <p><strong>Role:</strong> ${account.role || 'User'}</p>
        <p><strong>Subscription:</strong> ${account.sub_code}</p>
        <p><strong>Created:</strong> ${account.createdDate}</p>
        ${isCurrentAdmin ? '<span class="current-badge">Current Account</span>' : ''}
          </div>
          <div class="account-actions">
        ${!isCurrentAdmin ? `
            <button class="btn-delete" onclick="deleteAccount(${account.accountID}, '${account.email}')">
        Delete Account
            </button>
        ` : '<p style="color: #999; font-size: 12px;">Cannot delete your own account</p>'}
          </div>
      </div>
      `;
  }).join('');
}

// Delete account (Admin only)
async function deleteAccount(accountID, email) {
  if (!confirm(`Are you sure you want to delete account "${email}"?\n\nThis will delete:\n- The account\n- All profiles under this account\n- All ratings by those profiles\n\nThis action cannot be undone!`)) {
      return;
  }
  
  try {
      await fetch(`${API_BASE_URL}/accounts/${accountID}`, {
      method: 'DELETE',
      headers: {
          'X-User-Role': 'ADMIN',
          'X-Account-Code': currentUserCode
      }
      });
      
      showMessage(`Account "${email}" deleted successfully!`, 'success');
      
      // Reload accounts list
      await loadAllAccounts();
      
  } catch (error) {
      showMessage('Failed to delete account. Please try again.', 'error');
      console.error('Delete account error:', error);
  }
}

// Load initial data when page loads
window.addEventListener('load', () => {
});

