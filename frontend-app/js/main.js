// src/main/resources/static/js/main.js
import { handleRegister } from './register.js';
import { handleLogin } from './login.js';
import { loadDashboard } from './dashboard.js';
import { loadJumps } from './jumps.js';


// Central loader and event attachment

document.addEventListener('DOMContentLoaded', () => {

    // Register page
    const registerForm = document.getElementById('register-form');
    if (registerForm) registerForm.addEventListener('submit', handleRegister);

    // Login page
    console.log('DOM loaded, initializing...');
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        console.log('Login form found, attaching event listener');
        loginForm.addEventListener('submit', (event) => {
            console.log('Submit event triggered');
            handleLogin(event);
        }, { once: true });
    } else {
        console.error('Login form not found');
    }

    // Dashboard page
    const dashboardContainer = document.getElementById('dashboard-container');
    if (dashboardContainer) loadDashboard();

    // Jumps page
    const jumpsList = document.getElementById('jumps-list');
    if (jumpsList) loadJumps();
});
