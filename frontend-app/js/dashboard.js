// src/main/resources/static/js/dashboard.js
import { fetchWithAuth } from './api.js';
import { parseJwt } from './jwt.js';

// State
let sortColumn = 'jumpNumber';
let sortDirection = 'desc';
let currentPage = 0;
let currentSort = 'jumpDate,desc';
let jumpsData = [];
let totalPages = 0;

export async function loadDashboard() {
    const container = document.getElementById('dashboard-container');
    if (!container) return console.error('Dashboard container not found');

    const token = localStorage.getItem('jwtToken');
    if (!token) return redirectToLogin();

    const payload = parseJwt(token);
    if (!payload || !payload.sub) return redirectToLogin();

    // const username = payload.sub;
    // document.getElementById('welcome-message').textContent = `Hello ${username}`;

    try {
        // Fetch totals
        const [totalJumpsRes, totalFreefallRes] = await Promise.all([
            fetchWithAuth('http://localhost:8080/api/jumps/totaljumps', { method: 'GET' }),
            fetchWithAuth('http://localhost:8080/api/jumps/totalfreefall', { method: 'GET' })
        ]);

        document.getElementById('total-jumps').textContent = totalJumpsRes.ok ? await totalJumpsRes.json() : 'Error loading jumps';
        document.getElementById('total-freefall').textContent = totalFreefallRes.ok ? await totalFreefallRes.text() : 'Error loading freefall';

        // Fetch first page just to determine totalPages
        const firstPageRes = await fetchWithAuth(`http://localhost:8080/api/jumps/all?page=0&size=10&sort=${currentSort}`, { method: 'GET' });
        if (!firstPageRes.ok) return handleUnauthorized();

        const firstPage = await firstPageRes.json();
        totalPages = firstPage.totalPages;
        currentPage = 0;

        // Fetch page
        await fetchPage(currentPage);

    } catch (err) {
        console.error('Fetch error:', err);
        document.getElementById('jumps-table-body').innerHTML = '<tr><td colspan="9">Network error.</td></tr>';
    }

    document.getElementById('create-jump').addEventListener('click', () => {
        window.location.href = 'jumps.html';
    });
}

function redirectToLogin() {
    localStorage.removeItem('jwtToken');
    window.location.href = 'index.html';
}

function handleUnauthorized() {
    document.getElementById('jumps-table-body').innerHTML = '<tr><td colspan="9">Unauthorized. Please login again.</td></tr>';
    localStorage.removeItem('jwtToken');
    setTimeout(() => (window.location.href = 'index.html'), 2000);
}

// Render function (same corrected version)
function renderJumpsTable(jumps, page) {
    const tbody = document.getElementById('jumps-table-body');
    tbody.innerHTML = '';
    jumpsData = [...jumps];

    // Client-side sort only for jumpNumber | Ordinal Number Calculated / Not DB Persisted
    
    if (sortColumn === 'jumpNumber') {
        jumpsData.sort((a, b) =>
            sortDirection === 'asc' ? a.jumpNumber - b.jumpNumber : b.jumpNumber - a.jumpNumber
        );
    }
    

    jumpsData.forEach(jump => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td data-label="Jump Num."><span class="cell-value">${jump.jumpNumber}</span></td>
            <td data-label="Altitude"><span class="cell-value">${jump.altitude}</span></td>
            <td data-label="Free Fall"><span class="cell-value">${jump.freeFallDuration}</span></td>
            <td data-label="Jump Date"><span class="cell-value">${new Date(jump.jumpDate).toLocaleString('el-GR', { dateStyle: 'short'})}</span></td>
            <td data-label="Notes"><span class="cell-value">${jump.jumpNotes || ''}</span></td>
            <td data-label="Aircraft"><span class="cell-value">${jump.aircraft.aircraftName}</span></td>
            <td data-label="Dropzone"><span class="cell-value">${jump.dropzone.dropzoneName}</span></td>
            <td data-label="Jump Type"><span class="cell-value">${jump.jumptype.jumptypeName}</span></td>
            <td class="justify-content-between align-items-center h-100">
                <a class="edit-btn text-primary" data-jump-id="${jump.id}" title="Edit"><i class="fa-solid fa-pen-to-square fs-5"></i></a>
                <a class="delete-btn text-danger" data-jump-id="${jump.id}" title="Delete"><i class="fa-solid fa-trash fs-5"></i></a>
            </td>
        `;
        tbody.appendChild(tr);
    });


    // Attach even listener to delete buttons
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', () => handleDelete(btn.dataset.jumpId))
    });

    // Attach even listener to edit buttons
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', () => handleEdit(btn.dataset.jumpId))
    });

    // Pagination
    document.getElementById('prev-page').disabled = page === 0;
    document.getElementById('next-page').disabled = page >= totalPages - 1;
    document.getElementById('page-numbers').textContent = `Page ${page + 1} of ${totalPages}`;
    document.getElementById('prev-page').onclick = () => fetchPage(page - 1);
    document.getElementById('next-page').onclick = () => fetchPage(page + 1);

    // Sorting headers
    document.querySelectorAll('#jumps-table th[data-sort]').forEach(th => {
        th.style.cursor = 'pointer';
        th.onclick = () => {
            const column = th.getAttribute('data-sort');
            if (column === sortColumn) sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
            else { sortColumn = column; sortDirection = 'asc'; }

            if (column === 'jumpNumber') {
                renderJumpsTable(jumpsData, page);
            } else {
                currentSort = `${column},${sortDirection}`;
                fetchPage(currentPage);
            }
        };
    });

    // Reset Sorting Button
    document.getElementById('reset-sorting').addEventListener('click', () => {
    sortColumn = 'jumpNumber';
    sortDirection = 'desc';
    currentSort = 'jumpDate,desc';
    currentPage = 0;

    // Fetch first page again
    fetchPage(currentPage);
    });

}

// Handle delete action for a jump
async function handleDelete(jumpId) {
    const confirmDelete = confirm (`🪂 Are you sure you want to delete jump with database id #${jumpId}?`);

    if(!confirmDelete) {
        return // user cancelled
    }


    try {
        const response = await fetchWithAuth(`http://localhost:8080/api/jumps/${jumpId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Failed to delete jump');
        }

        //Refresh Jump list
        alert('✅ Jump deleted successfully!');
        await loadDashboard();
    } catch (error) {
        console.error('Error deleting jump: ', error);
        alert('❌ Could not delete jump. Please try again.');
    }
}


// Handle edit action for a jump
async function handleEdit(jumpId) {
    window.location.href = `jumps.html?edit=${jumpId}`;
}


// Fetch page with backend sort
async function fetchPage(page) {
    try {
        const response = await fetchWithAuth(`http://localhost:8080/api/jumps/all?page=${page}&size=10&sort=${currentSort}`, { method: 'GET' });
        if (response.ok) {
            const data = await response.json();
            currentPage = page;
            totalPages = data.totalPages;
            renderJumpsTable(data.content, page);
        } else {
            console.error('Page fetch failed:', response.status);
            document.getElementById('jumps-table-body').innerHTML = '<tr><td colspan="9">Error loading page.</td></tr>';
        }
    } catch (err) {
        console.error('Fetch error:', err);
        document.getElementById('jumps-table-body').innerHTML = '<tr><td colspan="9">Network error.</td></tr>';
    }
}

