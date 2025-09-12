// src/main/resources/static/js/dashboard.js
import { fetchWithAuth } from './api.js';
import { parseJwt } from './jwt.js';

export async function loadDashboard() {
    const container = document.getElementById('dashboard-container');
    if (!container) {
            console.error('Dashboard container not found');
            return;
        }

    const token = localStorage.getItem('jwtToken');
    console.log('Token:', token ? 'Found' : 'Not found');

    if (!token) {
        console.log('No token, redirecting to index.html');
        window.location.href = 'index.html';
        return;
    }

    const payload = parseJwt(token);
    console.log('JWT Payload:', payload);
    if (!payload || !payload.sub) {
        console.log('Invalid payload or no username, clearing token and redirecting');
        localStorage.removeItem('jwtToken');
        window.location.href = 'index.html';
        return;
    }

    const username = payload.sub; // extract username from JWT
    console.log('Fetching user data for Username:', username);
    const welcomeMessage = document.getElementById('welcome-message');
    welcomeMessage.textContent = `Hello ${username}`;


try {
        // Fetch totals & Jump Table
        const [totalJumpsRes, totalFreefallRes, jumpsRes] = await Promise.all([
            fetchWithAuth('http://localhost:8080/api/jumps/totaljumps', { method: 'GET' }),
            fetchWithAuth('http://localhost:8080/api/jumps/totalfreefall', { method: 'GET' }),
            fetchWithAuth('http://localhost:8080/api/jumps/all?page=0&size=10', { method: 'GET' })
        ]);

        // Handle totals
        if (totalJumpsRes.ok) {
            const totalJumps = await totalJumpsRes.json();
            document.getElementById('total-jumps').textContent = totalJumps;
        } else {
            document.getElementById('total-jumps').textContent = 'Error loading jumps';
        }

        if (totalFreefallRes.ok) {
            const totalFreefall = await totalFreefallRes.text();
            document.getElementById('total-freefall').textContent = totalFreefall;
        } else {
            document.getElementById('total-freefall').textContent = 'Error loading freefall';
        }

        // Handle jumps table
        if (jumpsRes.ok) {
            const data = await jumpsRes.json();
            console.log('Jumps data:', data);
            renderJumpsTable(data.content, data.number, data.totalPages);
        } else {
            console.error('Jumps fetch failed:', jumpsRes.status);
            document.getElementById('jumps-table-body').innerHTML = '<tr><td colspan="9">Unauthorized. Please login again.</td></tr>';
            localStorage.removeItem('jwtToken');
            setTimeout(() => window.location.href = 'index.html', 2000);
            return;
        }
    } catch (err) {
        console.error('Fetch error:', err);
        document.getElementById('jumps-table-body').innerHTML = '<tr><td colspan="9">Network error.</td></tr>';
    }

    // Create Jump button
    document.getElementById('create-jump').addEventListener('click', () => {
        window.location.href = 'jumps.html';
    });

}

// State for sorting
let sortColumn = 'jumpNumber';
let sortDirection = 'asc';
let currentPage = 0;
let jumpsData = [];

function renderJumpsTable(jumps, page, totalPages) {
    const tbody = document.getElementById('jumps-table-body');
    tbody.innerHTML = '';

    // Sort jumps
    jumpsData = [...jumps];
    jumpsData.sort((a, b) => {
        let valueA = getNestedValue(a, sortColumn);
        let valueB = getNestedValue(b, sortColumn);
        if (sortColumn === 'jumpDate') {
            valueA = new Date(valueA).getTime();
            valueB = new Date(valueB).getTime();
        }
        if (typeof valueA === 'string') valueA = valueA.toLowerCase();
        if (typeof valueB === 'string') valueB = valueB.toLowerCase();
        return sortDirection === 'asc' ? (valueA > valueB ? 1 : -1) : (valueA < valueB ? 1 : -1);
    });

    // Render table rows
    jumpsData.forEach(jump => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${jump.jumpNumber}</td>
            <td>${jump.altitude}</td>
            <td>${jump.freeFallDuration}</td>
            <td>${new Date(jump.jumpDate).toLocaleString('en-US', { dateStyle: 'short', timeStyle: 'short' })}</td>
            <td>${jump.jumpNotes || ''}</td>
            <td>${jump.aircraft.aircraftName}</td>
            <td>${jump.dropzone.dropzoneName}</td>
            <td>${jump.jumptype.jumptypeName}</td>
            <td>
                <a href="jumps.html?edit=${jump.id}">Edit</a> |
                <a href="jumps.html?delete=${jump.id}" class="delete-jump">Delete</a>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // Pagination controls
    const prevButton = document.getElementById('prev-page');
    const nextButton = document.getElementById('next-page');
    const pageNumbers = document.getElementById('page-numbers');

    prevButton.disabled = page === 0;
    nextButton.disabled = page >= totalPages - 1;
    pageNumbers.textContent = `Page ${page + 1} of ${totalPages}`;

    prevButton.onclick = () => fetchPage(page - 1);
    nextButton.onclick = () => fetchPage(page + 1);

    // Sorting event listeners
    document.querySelectorAll('#jumps-table th[data-sort]').forEach(th => {
        th.style.cursor = 'pointer';
        th.onclick = () => {
            const column = th.getAttribute('data-sort');
            if (column === sortColumn) {
                sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
            } else {
                sortColumn = column;
                sortDirection = 'asc';
            }
            renderJumpsTable(jumpsData, page, totalPages);
        };
    });
}

async function fetchPage(page) {
    try {
        const response = await fetchWithAuth(`http://localhost:8080/api/jumps/all?page=${page}&size=10`, { method: 'GET' });
        if (response.ok) {
            const data = await response.json();
            currentPage = page;
            renderJumpsTable(data.content, data.number, data.totalPages);
        } else {
            console.error('Page fetch failed:', response.status);
            document.getElementById('jumps-table-body').innerHTML = '<tr><td colspan="9">Error loading page.</td></tr>';
        }
    } catch (err) {
        console.error('Fetch error:', err);
        document.getElementById('jumps-table-body').innerHTML = '<tr><td colspan="9">Network error.</td></tr>';
    }
}

function getNestedValue(obj, path) {
    return path.split('.').reduce((o, key) => o && o[key], obj);
}

