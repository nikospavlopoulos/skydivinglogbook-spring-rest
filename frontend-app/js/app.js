// // src/main/resources/static/js/app.js
//
// // --- Register ---
// async function handleRegister(event) {
//     event.preventDefault();
//
//     const username = document.getElementById('register-username').value;
//     const firstname = document.getElementById('register-firstname').value || null;
//     const lastname = document.getElementById('register-lastname').value || null;
//     const password = document.getElementById('register-password').value;
//     const confirmPassword = document.getElementById('register-confirm-password').value;
//     const messageDiv = document.getElementById('register-message');
//
//     messageDiv.textContent = '';
//
//     if (password !== confirmPassword) {
//         messageDiv.textContent = 'Passwords do not match';
//         return;
//     }
//
//     const payload = {
//         username,
//         password,
//         firstname,
//         lastname
//     };
//
//     try {
//         const response = await fetch('/api/users', {
//             method: 'POST',
//             headers: { 'Content-Type': 'application/json' },
//             body: JSON.stringify(payload)
//         });
//
//         if (response.ok) {
//             messageDiv.textContent = 'Registration successful! Redirecting to login...';
//             messageDiv.classList.add('success');
//             setTimeout(() => window.location.href = 'index.html', 2000);
//         } else {
//             const error = await response.json();
//             messageDiv.textContent = error.message || 'Registration failed';
//         }
//     } catch (err) {
//         messageDiv.textContent = 'Network error';
//         console.error(err);
//     }
// }
//
// // --- Login ---
// async function handleLogin(event) {
//     event.preventDefault();
//
//     const usernameOrEmail = document.getElementById('login-username').value;
//     const password = document.getElementById('login-password').value;
//     const messageDiv = document.getElementById('login-message');
//
//     messageDiv.textContent = '';
//
//     const payload = { username: usernameOrEmail, password };
//
//     try {
//         const response = await fetch('/api/auth/login', {
//             method: 'POST',
//             headers: { 'Content-Type': 'application/json' },
//             body: JSON.stringify(payload)
//         });
//
//         if (response.ok) {
//             const data = await response.json();
//             localStorage.setItem('jwtToken', data.token);
//             window.location.href = 'dashboard.html'; // placeholder for future dashboard
//         } else {
//             const error = await response.json();
//             messageDiv.textContent = error.message || 'Login failed';
//         }
//     } catch (err) {
//         messageDiv.textContent = 'Network error';
//         console.error(err);
//     }
// }
//
// // Attach events if forms exist
// document.addEventListener('DOMContentLoaded', () => {
//     const registerForm = document.getElementById('register-form');
//     if (registerForm) registerForm.addEventListener('submit', handleRegister);
//
//     const loginForm = document.getElementById('login-form');
//     if (loginForm) loginForm.addEventListener('submit', handleLogin);
// });
