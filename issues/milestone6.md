## Milestone 6 – Frontend UI – Basic Client for the REST API

- [ ] Create static frontend project (index.html, style.css, script.js).
- [ ] Create homepage with navigation links.
- [ ] Implement fetch API calls to backend.
- [ ] Create login form (call /api/v1/auth/login).
- [ ] Create registration form.
- [ ] List jumps in table (call /api/v1/jumps).
- [ ] Add form validation in JS.
- [ ] Display error messages.
- [ ] Apply minimal CSS for usability.
- [ ] Document UI usage.

	#### General Tasks

	- Build a minimal frontend UI to interact with the REST API.
	- Use only **HTML, CSS, and vanilla JavaScript*- (no frameworks).
	- Focus on learning how to consume the API from a browser client.
	- Keep the design simple, with emphasis on functionality.

	#### Specific/Detailed Tasks

	- Set up a basic static frontend project structure (`index.html`, `style.css`, `script.js`).
	- Create a simple homepage with navigation links (Users, Jumps, Lookups).
	- Implement **fetch API calls*- to interact with the backend:
		- Login form → call `/api/v1/auth/login`, store JWT in `localStorage`.
		- User registration form → call `/api/v1/auth/register`.
		- List Jumps → fetch `/api/v1/jumps`, render in HTML table.
	- Add form validation before sending requests (basic JS checks).
	- Display error messages consistently based on backend error responses.
	- Use minimal CSS for layout and usability (responsive if possible).
	- Test the UI against the running backend (via Docker if milestone 6 complete).
	- Document how to start the UI and how it connects to the API.
