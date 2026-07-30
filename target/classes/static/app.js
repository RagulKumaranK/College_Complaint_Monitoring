/* ========================================================
   Campus Fix — Application Logic & API Client
   ======================================================== */

const API_BASE = '/api';

// State Management
let state = {
    user: null,
    token: localStorage.getItem('jwt_token') || null,
    currentPage: 0,
    pageSize: 10,
    totalPages: 0,
    currentComplaint: null,
    searchDebounceTimer: null
};

// ==================== App Initialization ====================
document.addEventListener('DOMContentLoaded', () => {
    if (state.token) {
        // Recover user info from localStorage if present
        const savedUser = localStorage.getItem('user_info');
        if (savedUser) {
            state.user = JSON.parse(savedUser);
            updateUserUI();
            closeModal('login-modal');
            loadDashboardData();
            loadComplaintsData();
        } else {
            showLoginModal();
        }
    } else {
        showLoginModal();
    }
});

// ==================== Navigation & Sections ====================
function showSection(sectionId) {
    document.querySelectorAll('.page-section').forEach(sec => sec.classList.remove('active'));
    document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));

    const targetSec = document.getElementById(sectionId);
    if (targetSec) targetSec.classList.add('active');

    if (sectionId === 'dashboard-section') {
        document.getElementById('btn-nav-dashboard').classList.add('active');
        loadDashboardData();
    } else if (sectionId === 'complaints-section') {
        document.getElementById('btn-nav-complaints').classList.add('active');
        loadComplaintsData();
    }
}

// ==================== Authentication Logic ====================
function showLoginModal() {
    document.getElementById('login-modal').classList.remove('hidden');
}

function quickLogin(email, password) {
    document.getElementById('login-email').value = email;
    document.getElementById('login-password').value = password;
    handleLoginSubmit(new Event('submit'));
}

async function handleLoginSubmit(event) {
    if (event) event.preventDefault();

    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const result = await response.json();

        if (response.ok && result.success) {
            state.token = result.data.token;
            state.user = {
                email: result.data.email,
                fullName: result.data.fullName,
                role: result.data.role
            };

            localStorage.setItem('jwt_token', state.token);
            localStorage.setItem('user_info', JSON.stringify(state.user));

            updateUserUI();
            closeModal('login-modal');
            showToast(`Welcome back, ${state.user.fullName}!`, 'success');
            showSection('dashboard-section');
        } else {
            showToast(result.message || 'Authentication failed', 'error');
        }
    } catch (err) {
        showToast('Server error during login', 'error');
    }
}

function handleLogout() {
    state.token = null;
    state.user = null;
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_info');
    showLoginModal();
    showToast('Logged out successfully', 'success');
}

function updateUserUI() {
    if (!state.user) return;

    document.getElementById('user-name').innerText = state.user.fullName;
    document.getElementById('user-role').innerText = state.user.role;
    document.getElementById('user-avatar').innerText = state.user.fullName.charAt(0);

    const roleBadge = document.getElementById('user-role');
    roleBadge.className = state.user.role === 'ADMIN' ? 'user-role badge-admin' : 'user-role badge-user';

    // Show/Hide Admin elements
    const isAdmin = state.user.role === 'ADMIN';
    document.getElementById('admin-action-box').style.display = isAdmin ? 'block' : 'none';
    document.getElementById('btn-export-csv').style.display = isAdmin ? 'inline-flex' : 'none';
    document.getElementById('btn-export-excel').style.display = isAdmin ? 'inline-flex' : 'none';
}

// ==================== Dashboard Metrics ====================
async function loadDashboardData() {
    if (!state.token) return;

    try {
        const response = await fetch(`${API_BASE}/admin/dashboard`, {
            headers: getAuthHeaders()
        });

        if (response.status === 403) {
            // User role - fetch user complaints stats
            loadUserDashboardStats();
            return;
        }

        const result = await response.json();
        if (result.success && result.data) {
            const d = result.data;
            document.getElementById('stat-total').innerText = d.totalComplaints;
            document.getElementById('stat-open').innerText = d.openComplaints;
            document.getElementById('stat-progress').innerText = d.inProgressComplaints + d.assignedComplaints;
            document.getElementById('stat-resolved').innerText = d.resolvedComplaints + d.closedComplaints;
            document.getElementById('stat-critical').innerText = d.criticalComplaints;

            renderBarChart('category-chart', d.complaintsByCategory, d.totalComplaints);
            renderBarChart('building-chart', d.complaintsByBuilding, d.totalComplaints);
        }
    } catch (err) {
        console.error('Failed to load dashboard data:', err);
    }
}

async function loadUserDashboardStats() {
    // For non-admin users, query their complaints
    try {
        const response = await fetch(`${API_BASE}/complaints?size=100`, {
            headers: getAuthHeaders()
        });
        const result = await response.json();
        if (result.success && result.data) {
            const list = result.data.content || [];
            document.getElementById('stat-total').innerText = list.length;
            document.getElementById('stat-open').innerText = list.filter(c => c.status === 'OPEN').length;
            document.getElementById('stat-progress').innerText = list.filter(c => c.status === 'IN_PROGRESS' || c.status === 'ASSIGNED').length;
            document.getElementById('stat-resolved').innerText = list.filter(c => c.status === 'RESOLVED' || c.status === 'CLOSED').length;
            document.getElementById('stat-critical').innerText = list.filter(c => c.priority === 'CRITICAL').length;
        }
    } catch (err) {
        console.error(err);
    }
}

function renderBarChart(containerId, dataMap, total) {
    const container = document.getElementById(containerId);
    if (!container) return;

    if (!dataMap || Object.keys(dataMap).length === 0) {
        container.innerHTML = '<p class="page-desc">No data points available yet.</p>';
        return;
    }

    let html = '';
    const maxVal = Math.max(...Object.values(dataMap), 1);

    for (const [key, val] of Object.entries(dataMap)) {
        const percentage = Math.round((val / maxVal) * 100);
        html += `
            <div class="chart-bar-item">
                <div class="chart-bar-label">
                    <span>${key}</span>
                    <span><strong>${val}</strong></span>
                </div>
                <div class="chart-bar-bg">
                    <div class="chart-bar-fill" style="width: ${percentage}%"></div>
                </div>
            </div>
        `;
    }
    container.innerHTML = html;
}

// ==================== Complaints List & Filtering ====================
async function loadComplaintsData() {
    if (!state.token) return;

    const keyword = document.getElementById('filter-keyword').value;
    const category = document.getElementById('filter-category').value;
    const status = document.getElementById('filter-status').value;
    const priority = document.getElementById('filter-priority').value;

    let queryParams = `page=${state.currentPage}&size=${state.pageSize}&sortBy=createdAt&sortDir=desc`;
    if (keyword) queryParams += `&keyword=${encodeURIComponent(keyword)}`;
    if (category) queryParams += `&category=${category}`;
    if (status) queryParams += `&status=${status}`;
    if (priority) queryParams += `&priority=${priority}`;

    try {
        const response = await fetch(`${API_BASE}/complaints?${queryParams}`, {
            headers: getAuthHeaders()
        });

        const result = await response.json();

        if (response.ok && result.success) {
            renderComplaintsTable(result.data.content);
            updatePaginationUI(result.data);
        }
    } catch (err) {
        showToast('Failed to load complaints', 'error');
    }
}

function renderComplaintsTable(complaints) {
    const tbody = document.getElementById('complaints-table-body');
    if (!tbody) return;

    if (!complaints || complaints.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" style="text-align: center; padding: 2rem; color: var(--text-muted);">
                    <i class="fa-solid fa-folder-open" style="font-size: 2rem; margin-bottom: 0.5rem;"></i>
                    <p>No complaints found matching criteria.</p>
                </td>
            </tr>
        `;
        return;
    }

    let html = '';
    complaints.forEach(c => {
        html += `
            <tr>
                <td>#${c.id}</td>
                <td>
                    <strong>${escapeHtml(c.title)}</strong>
                    <div style="font-size: 0.75rem; color: var(--text-muted);">${c.roomNumber ? c.roomNumber + ' • ' : ''}${c.building}</div>
                </td>
                <td><span class="badge badge-outline">${c.category}</span></td>
                <td>${escapeHtml(c.building)}</td>
                <td><span class="priority-badge priority-${c.priority}"><i class="fa-solid fa-circle" style="font-size: 6px;"></i> ${c.priority}</span></td>
                <td><span class="badge badge-${c.status}">${c.status}</span></td>
                <td>${escapeHtml(c.reportedByName || 'N/A')}</td>
                <td>${c.assignedTo ? escapeHtml(c.assignedTo) : '<span style="color: var(--text-muted)">Unassigned</span>'}</td>
                <td>
                    <button class="btn btn-outline" style="padding: 0.3rem 0.6rem; font-size: 0.75rem;" onclick="openViewComplaintModal(${c.id})">
                        <i class="fa-solid fa-eye"></i> View
                    </button>
                </td>
            </tr>
        `;
    });
    tbody.innerHTML = html;
}

function updatePaginationUI(pageData) {
    state.totalPages = pageData.totalPages;
    document.getElementById('pagination-info').innerText = 
        `Showing ${pageData.numberOfElements} of ${pageData.totalElements} complaints`;
    document.getElementById('current-page-badge').innerText = `Page ${pageData.number + 1} of ${Math.max(pageData.totalPages, 1)}`;

    document.getElementById('btn-prev').disabled = pageData.first;
    document.getElementById('btn-next').disabled = pageData.last;
}

function changePage(delta) {
    const newPage = state.currentPage + delta;
    if (newPage >= 0 && newPage < state.totalPages) {
        state.currentPage = newPage;
        loadComplaintsData();
    }
}

function debounceSearch() {
    clearTimeout(state.searchDebounceTimer);
    state.searchDebounceTimer = setTimeout(() => {
        state.currentPage = 0;
        loadComplaintsData();
    }, 300);
}

function resetFilters() {
    document.getElementById('filter-keyword').value = '';
    document.getElementById('filter-category').value = '';
    document.getElementById('filter-status').value = '';
    document.getElementById('filter-priority').value = '';
    state.currentPage = 0;
    loadComplaintsData();
}

// ==================== Create & Manage Modal Logic ====================
function openNewComplaintModal() {
    document.getElementById('new-complaint-modal').classList.remove('hidden');
}

async function handleCreateComplaint(event) {
    event.preventDefault();

    const payload = {
        title: document.getElementById('comp-title').value,
        category: document.getElementById('comp-category').value,
        priority: document.getElementById('comp-priority').value,
        building: document.getElementById('comp-building').value,
        roomNumber: document.getElementById('comp-room').value,
        description: document.getElementById('comp-desc').value
    };

    try {
        const response = await fetch(`${API_BASE}/complaints`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        if (response.ok && result.success) {
            showToast('Complaint reported successfully!', 'success');
            closeModal('new-complaint-modal');
            document.getElementById('new-complaint-form').reset();
            loadComplaintsData();
            loadDashboardData();
        } else {
            showToast(result.message || 'Failed to submit complaint', 'error');
        }
    } catch (err) {
        showToast('Server error while creating complaint', 'error');
    }
}

async function openViewComplaintModal(id) {
    try {
        const response = await fetch(`${API_BASE}/complaints/${id}`, {
            headers: getAuthHeaders()
        });

        const result = await response.json();

        if (response.ok && result.success) {
            state.currentComplaint = result.data;
            const c = result.data;

            document.getElementById('detail-id').innerText = c.id;
            document.getElementById('detail-title').innerText = c.title;
            document.getElementById('detail-location').innerText = `${c.building}${c.roomNumber ? ' (' + c.roomNumber + ')' : ''}`;
            document.getElementById('detail-reporter').innerText = `${c.reportedByName} (${c.reportedByEmail})`;
            document.getElementById('detail-assigned').innerText = c.assignedTo || 'Unassigned';
            document.getElementById('detail-date').innerText = new Date(c.createdAt).toLocaleString();
            document.getElementById('detail-desc').innerText = c.description;
            document.getElementById('detail-remarks').innerText = c.remarks || 'No remarks provided.';

            // Badges
            document.getElementById('detail-status-badge').innerText = c.status;
            document.getElementById('detail-status-badge').className = `badge badge-${c.status}`;
            document.getElementById('detail-priority-badge').innerText = c.priority;
            document.getElementById('detail-priority-badge').className = `badge priority-${c.priority}`;
            document.getElementById('detail-category-badge').innerText = c.category;

            // Admin fields
            document.getElementById('admin-status-select').value = c.status;
            document.getElementById('admin-remarks-input').value = c.remarks || '';
            document.getElementById('admin-assign-input').value = c.assignedTo || '';

            document.getElementById('view-complaint-modal').classList.remove('hidden');
        }
    } catch (err) {
        showToast('Could not fetch complaint details', 'error');
    }
}

async function handleAdminStatusUpdate() {
    if (!state.currentComplaint) return;

    const status = document.getElementById('admin-status-select').value;
    const remarks = document.getElementById('admin-remarks-input').value;

    try {
        const response = await fetch(`${API_BASE}/admin/status/${state.currentComplaint.id}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify({ status, remarks })
        });

        const result = await response.json();
        if (response.ok && result.success) {
            showToast('Status updated successfully!', 'success');
            closeModal('view-complaint-modal');
            loadComplaintsData();
            loadDashboardData();
        } else {
            showToast(result.message || 'Status update failed', 'error');
        }
    } catch (err) {
        showToast('Error updating status', 'error');
    }
}

async function handleAdminAssign() {
    if (!state.currentComplaint) return;

    const assignedTo = document.getElementById('admin-assign-input').value;

    try {
        const response = await fetch(`${API_BASE}/admin/assign/${state.currentComplaint.id}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify({ assignedTo })
        });

        const result = await response.json();
        if (response.ok && result.success) {
            showToast('Task assigned successfully!', 'success');
            closeModal('view-complaint-modal');
            loadComplaintsData();
        } else {
            showToast(result.message || 'Assignment failed', 'error');
        }
    } catch (err) {
        showToast('Error assigning complaint', 'error');
    }
}

async function handleDeleteComplaint() {
    if (!state.currentComplaint) return;

    if (!confirm('Are you sure you want to delete this complaint?')) return;

    try {
        const response = await fetch(`${API_BASE}/complaints/${state.currentComplaint.id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        const result = await response.json();
        if (response.ok && result.success) {
            showToast('Complaint deleted', 'success');
            closeModal('view-complaint-modal');
            loadComplaintsData();
            loadDashboardData();
        } else {
            showToast(result.message || 'Delete failed', 'error');
        }
    } catch (err) {
        showToast('Error deleting complaint', 'error');
    }
}

// Export CSV / Excel
function exportData(type) {
    const url = `${API_BASE}/admin/export/${type}`;
    fetch(url, { headers: getAuthHeaders() })
        .then(res => res.blob())
        .then(blob => {
            const downloadUrl = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = downloadUrl;
            a.download = `complaints_export.${type === 'csv' ? 'csv' : 'xlsx'}`;
            document.body.appendChild(a);
            a.click();
            a.remove();
        })
        .catch(() => showToast('Export failed', 'error'));
}

// Helpers & Utilities
function getAuthHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${state.token}`
    };
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.add('hidden');
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<i class="fa-solid fa-${type === 'success' ? 'circle-check' : 'circle-xmark'}"></i> ${escapeHtml(message)}`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 4000);
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"']/g, m => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    }[m]));
}
