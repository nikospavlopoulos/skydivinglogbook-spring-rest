import { fetchWithAuth } from './api.js';

// Detect edit mode vs create mode
const urlParams = new URLSearchParams(window.location.search);
const editId = urlParams.get('edit');

if (editId) {
    console.log(`Edit mode detected for jump with DB id: ${editId}`);
    loadJumpForEdit(editId);
} else {
    console.log('Create mode detected (Create New Jump)')
}

// Edit mode: Load Existing Jump data (prefill form)
async function loadJumpForEdit(jumpId) {
    await loadLookups();

    try {
        const response = await fetchWithAuth(`http://localhost:8080/api/jumps/${jumpId}`);

        const jump = await response.json();
        

        // Prefill form fields
        document.getElementById('altitude').value = jump.altitude;
        document.getElementById('freeFallDuration').value = jump.freeFallDuration;
        document.getElementById('jumpDate').value = jump.jumpDate.split('T')[0]; //retrieve only date, not time
        document.getElementById('aircraftId').value = jump.aircraft.id;
        document.getElementById('dropzoneId').value = jump.dropzone.id;
        document.getElementById('jumptypeId').value = jump.jumptype.id; 
        document.getElementById('jumpNotes').value = jump.jumpNotes;

        // Change page header to Edit Jump
        document.querySelector('#header').textContent =  `Edit Jump with DB id #${jumpId}`;

        // Change button text to 'Update'
        document.querySelector('#jump-form button[type="submit"]').textContent = 'Update Jump'
    } catch (error) {
        console.error('Error loading the jump for edit: ', error);
        alert ('❌ Could not load jump details for editing.');
    }
}

// Edit mode: send Put `api/jumps/${editId}` to Update jump
async function putUpdateJump(editId) {

    try {
        // Serialize form to JumpUpdateDTO
        const jumpData = {
            altitude: parseInt(document.getElementById('altitude').value),
            freeFallDuration: parseInt(document.getElementById('freeFallDuration').value),
            jumpDate: document.getElementById('jumpDate').value + 'T00:00:00',
            jumpNotes: document.getElementById('jumpNotes').value || null,
            aircraftId: parseInt(document.getElementById('aircraftId').value),
            dropzoneId: parseInt(document.getElementById('dropzoneId').value),
            jumptypeId: parseInt(document.getElementById('jumptypeId').value)
        }; 
        
        const response = await fetchWithAuth(`http://localhost:8080/api/jumps/${editId}`, {
            method: 'PUT',
            body: JSON.stringify(jumpData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `HTTP ${response.status}`);
        }

        // Redirect to dashboard with success alert
        alert('✅ Jump updated successfully!');        
        window.location.href = 'dashboard.html';
    } catch (error) {
        console.error('Error updating jump: ', error);
        alert ('❌ Could not update jump:' + error.message);

    }

}

    // Fetch lookup data for dropdowns
export async function loadLookups() {
        try {
            const [dropzoneRes, aircraftRes, jumptypeRes] = await Promise.all([
                fetchWithAuth('http://localhost:8080/api/lookups/dropzones'),
                fetchWithAuth('http://localhost:8080/api/lookups/aircraft'),
                fetchWithAuth('http://localhost:8080/api/lookups/jumptypes')
            ]);

            // Check response status
            if (!dropzoneRes.ok) throw new Error(`Failed to load dropzones: HTTP ${dropzoneRes.status}`);
            if (!aircraftRes.ok) throw new Error(`Failed to load aircraft: HTTP ${aircraftRes.status}`);
            if (!jumptypeRes.ok) throw new Error(`Failed to load jumptypes: HTTP ${jumptypeRes.status}`);

            const dropzones = await dropzoneRes.json();
            const aircraft = await aircraftRes.json();
            const jumptypes = await jumptypeRes.json();

            // Populate dropdowns
            const dropzoneSelect = document.getElementById('dropzoneId');
            dropzoneSelect.innerHTML = '<option value="">Select Dropzone</option>'; // reset to avoid duplicates
            dropzones.forEach(dz => {
                const option = document.createElement('option');
                option.value = dz.id;
                option.textContent = dz.dropzoneName || 'Unknown Dropzone';
                dropzoneSelect.appendChild(option);
            });

            const aircraftSelect = document.getElementById('aircraftId');
            aircraftSelect.innerHTML = '<option value="">Select Aircraft</option>'; // reset to avoid duplicates
            aircraft.forEach(ac => {
                const option = document.createElement('option');
                option.value = ac.id;
                option.textContent = ac.aircraftName || 'Unknown Aircraft';
                aircraftSelect.appendChild(option);
            });

            const jumptypeSelect = document.getElementById('jumptypeId');
            jumptypeSelect.innerHTML = '<option value="">Select Jumptype</option>'; // reset to avoid duplicates
            jumptypes.forEach(jt => {
                const option = document.createElement('option');
                option.value = jt.id;
                option.textContent = jt.jumptypeName || 'Unknown Jump Type';
                jumptypeSelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error loading lookups:', error);
            messageDiv.textContent = 'Error loading dropdowns: ' + error.message;
            messageDiv.classList.add('error');
        }
    }

export async function loadJumps() {
    const jumpForm = document.getElementById('jump-form');
    const messageDiv = document.getElementById('message');
    const createdJumpDiv = document.getElementById('new-jump');
   
    // Client-side validation
    function validateField(field, value) {
        const errors = {
            altitude: () => {
                const num = parseInt(value);
                if (!value) return 'Altitude is required';
                if (num < 3000 || num > 20000) return 'Altitude must be between 3000 and 20000 ft';
                return '';
            },
            freeFallDuration: () => {
                const num = parseInt(value);
                if (!value) return 'Free fall duration is required';
                if (num < 0 || num > 100) return 'Free fall duration must be between 0 and 100 seconds';
                return '';
            },
            jumpDate: () => {
                if (!value) return 'Jump date is required';

                const [year, month, day] = value.split('-');
                const date = new Date(year, month - 1, day); // Local date at midnight

                if (date > new Date()) return 'Jump date must be in the past or present';
                return '';
            },
            jumpNotes: () => {
                if (value.length > 500) return 'Notes must be under 500 characters';
                return '';
            },
            aircraftId: () => (!value ? 'Aircraft is required' : ''),
            dropzoneId: () => (!value ? 'Dropzone is required' : ''),
            jumptypeId: () => (!value ? 'Jump type is required' : '')
        };
        return errors[field] ? errors[field]() : '';
    }

    // Attach blur validation
    ['altitude', 'freeFallDuration', 'jumpDate', 'jumpNotes', 'aircraftId', 'dropzoneId', 'jumptypeId'].forEach(id => {
        const input = document.getElementById(id);
        input.addEventListener('blur', () => {
            const errorSpan = document.getElementById(`${id}-error`);
            errorSpan.textContent = validateField(id, input.value);
        });
    });

    // Form submission
    jumpForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        messageDiv.textContent = '';
        createdJumpDiv.textContent = '';

        // Validate all fields
        const fields = ['altitude', 'freeFallDuration', 'jumpDate', 'jumpNotes', 'aircraftId', 'dropzoneId', 'jumptypeId'];
        let isValid = true;
        fields.forEach(id => {
            const input = document.getElementById(id);
            const error = validateField(id, input.value);
            document.getElementById(`${id}-error`).textContent = error;
            if (error) isValid = false;
        });

        if (!isValid) {
            messageDiv.textContent = 'Please fix validation errors';
            messageDiv.classList.add('error');
            return;
        }

        //// --- INSERT HERE THE UPDATE JUMP --- ///
        if (editId) {
            await putUpdateJump(editId);
            return; // stop any further actions
        }

        // Serialize form to JumpInsertDTO
        const jumpData = {
            altitude: parseInt(document.getElementById('altitude').value),
            freeFallDuration: parseInt(document.getElementById('freeFallDuration').value),
            jumpDate: document.getElementById('jumpDate').value + 'T00:00:00',
            jumpNotes: document.getElementById('jumpNotes').value || null,
            aircraftId: parseInt(document.getElementById('aircraftId').value),
            dropzoneId: parseInt(document.getElementById('dropzoneId').value),
            jumptypeId: parseInt(document.getElementById('jumptypeId').value)
        };

        try {
            const response = await fetchWithAuth('http://localhost:8080/api/jumps', {
                method: 'POST',
                body: JSON.stringify(jumpData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || `HTTP ${response.status}`);
            }

            const createdJump = await response.json();
            messageDiv.textContent = 'Jump created successfully!';
            messageDiv.classList.remove('error');

            // Fetch created jump details
            const jumpDetails = await fetchWithAuth(`http://localhost:8080/api/jumps/${createdJump.id}`).then(res => {
                if (!res.ok) throw new Error(`Failed to fetch jump: HTTP ${res.status}`);
                return res.json();
            });

            // Alert Success
            alert('✅ Jump Created successfully!');        


            // Display created jump
            createdJumpDiv.innerHTML = `
                <h3>Created Jump #${jumpDetails.jumpNumber}</h3>
                <p>Altitude: ${jumpDetails.altitude} ft</p>
                <p>Free Fall: ${jumpDetails.freeFallDuration} s</p>
                <p>Date: ${new Date(jumpDetails.jumpDate).toLocaleDateString()}</p>
                <p>Notes: ${jumpDetails.jumpNotes || 'None'}</p>
                <p>Aircraft: ${jumpDetails.aircraft.aircraftName}</p>
                <p>Dropzone: ${jumpDetails.dropzone.dropzoneName}</p>
                <p>Jump Type: ${jumpDetails.jumptype.jumptypeName}</p>
            `;

            // Clear form
            jumpForm.reset();
            fields.forEach(id => document.getElementById(`${id}-error`).textContent = '');
        } catch (error) {
            console.error('Error creating jump:', error);
            messageDiv.textContent = 'Error creating jump: ' + error.message;
            messageDiv.classList.add('error');
        }
    });


// Load dropdowns
await loadLookups(); 
}