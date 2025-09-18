import { fetchWithAuth, handleUnauthorized } from './api.js';
import { parseJwt } from './jwt.js';

export async function loadProfile() {
    const container = document.getElementById('profile-container');
    if (!container) return;

    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    const payload = parseJwt(token);
    const userId = payload.sub; // assume JWT sub = user ID

    try {
        // Fetch user info
        const response = await fetchWithAuth(`http://localhost:8080/api/users/${userId}`, { method: 'GET' });

        if (!response.ok) return handleUnauthorized(response);

        const user = await response.json();

        // Fill form fields
        document.getElementById('profile-username').value = user.username;
        document.getElementById('profile-firstname').value = user.firstname || '';
        document.getElementById('profile-lastname').value = user.lastname || '';

        // Form submission events
        document.getElementById('profile-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const updatedUser = {
                firstname: document.getElementById('profile-firstname').value,
                lastname: document.getElementById('profile-lastname').value
            };
            const updateResp = await fetchWithAuth(`http://localhost:8080/api/users/${userId}`, {
                method: 'PUT',
                body: JSON.stringify(updatedUser)
            });
            const msgDiv = document.getElementById('profile-message');
            msgDiv.textContent = updateResp.ok ? 'Profile updated successfully!' : 'Update failed';
            if (updateResp.ok) msgDiv.classList.add('success');
        });

        document.getElementById('password-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const password = document.getElementById('profile-password').value;
            const confirm = document.getElementById('profile-confirm-password').value;
            const msgDiv = document.getElementById('profile-message');

            if (password !== confirm) {
                msgDiv.textContent = 'Passwords do not match';
                return;
            }

            const updateResp = await fetchWithAuth(`http://localhost:8080/api/users/${userId}/password`, {
                method: 'PATCH',
                body: JSON.stringify({ password })
            });
            msgDiv.textContent = updateResp.ok ? 'Password updated successfully!' : 'Password update failed';
            if (updateResp.ok) msgDiv.classList.add('success');
        });

        document.getElementById('delete-account').addEventListener('click', async () => {
            if (!confirm('Are you sure you want to delete your account?')) return;

            const deleteResp = await fetchWithAuth(`http://localhost:8080/api/users/${userId}`, { method: 'DELETE' });
            if (deleteResp.ok) {
                localStorage.removeItem('jwtToken');
                window.location.href = 'index.html';
            } else {
                document.getElementById('profile-message').textContent = 'Failed to delete account';
            }
        });

    } catch (err) {
        console.error('Profile load error:', err);
        container.innerHTML = '<div>Network error.</div>';
    }
}
