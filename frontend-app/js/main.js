// src/main/resources/static/js/main.js
import { handleRegister } from './register.js';
import { handleLogin } from './login.js';
import { loadDashboard } from './dashboard.js';

// Central loader and event attachment

document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('register-form');
    if (registerForm) registerForm.addEventListener('submit', handleRegister);

    const loginForm = document.getElementById('login-form');
    if (loginForm) loginForm.addEventListener('submit', handleLogin);

    const dashboardContainer = document.getElementById('dashboard-container');
    if (dashboardContainer) loadDashboard();
});
