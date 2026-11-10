/**
 * Shared API client + UI helpers for the RozgarOne admin panel.
 *
 * Endpoint contract (see backend/admin_api/*.php):
 *   POST auth.php?action=login   {username,password} -> {status,user,csrf}
 *   GET  auth.php?action=me                           -> {status,user,csrf}
 *   POST auth.php?action=logout
 *   GET  posts.php?feed=&search=&page=&per_page=&scope= -> {status,posts,total,page,per_page,feeds}
 *   POST posts.php  {action:create|update|delete|restore, ...}
 *   GET|POST filters.php, notifications.php, settings.php
 * Every mutating POST sends the X-CSRF-Token header.
 */
(function () {
  'use strict';

  const BASE = '/admin_api/';
  let csrf = sessionStorage.getItem('csrf') || '';

  class ApiError extends Error {
    constructor(message, code) { super(message); this.code = code; }
  }

  async function request(path, options = {}) {
    const opts = { credentials: 'same-origin', headers: {}, ...options };
    if (opts.body !== undefined) {
      opts.method = opts.method || 'POST';
      opts.headers['Content-Type'] = 'application/json';
      opts.body = JSON.stringify(opts.body);
    }
    if ((opts.method || 'GET') === 'POST' && csrf) {
      opts.headers['X-CSRF-Token'] = csrf;
    }
    const res = await fetch(BASE + path, opts);
    if (res.status === 401) {
      sessionStorage.removeItem('csrf');
      if (!/index\.html$|\/$/.test(location.pathname)) location.href = 'index.html';
      throw new ApiError('Please sign in again', 401);
    }
    let data = {};
    try { data = await res.json(); } catch (e) { throw new ApiError('Invalid server response', res.status); }
    if (data.status !== 'true') {
      throw new ApiError(data.message || 'Request failed', res.status);
    }
    if (data.csrf) { csrf = data.csrf; sessionStorage.setItem('csrf', csrf); }
    return data;
  }

  const api = {
    login: (username, password) => request('auth.php?action=login', { body: { username, password } }),
    logout: () => request('auth.php?action=logout', { body: {} }),
    me: () => request('auth.php?action=me'),

    posts: (q = {}) => request('posts.php?' + new URLSearchParams(q)),
    savePost: (payload) => request('posts.php', { body: payload }),
    deletePost: (id) => request('posts.php', { body: { action: 'delete', id } }),
    restorePost: (id) => request('posts.php', { body: { action: 'restore', id } }),

    filters: (group) => request('filters.php' + (group ? '?group=' + encodeURIComponent(group) : '')),
    saveFilter: (payload) => request('filters.php', { body: payload }),
    deleteFilter: (id) => request('filters.php', { body: { action: 'delete', id } }),

    notifications: () => request('notifications.php'),
    saveNotification: (payload) => request('notifications.php', { body: payload }),
    deleteNotification: (id) => request('notifications.php', { body: { action: 'delete', id } }),

    settings: () => request('settings.php'),
    saveSettings: (payload) => request('settings.php', { body: payload }),
    changePassword: (payload) => request('settings.php', { body: { action: 'password', ...payload } }),
  };

  // ------------------------------------------------------------ UI helpers
  function toast(message, kind = '') {
    let zone = document.querySelector('.toast-zone');
    if (!zone) {
      zone = document.createElement('div');
      zone.className = 'toast-zone';
      document.body.appendChild(zone);
    }
    const el = document.createElement('div');
    el.className = 'toast ' + kind;
    el.textContent = message;
    zone.appendChild(el);
    setTimeout(() => { el.style.opacity = '0'; el.style.transition = 'opacity .3s'; }, 2600);
    setTimeout(() => el.remove(), 3000);
  }

  function confirmDialog(title, text) {
    return new Promise((resolve) => {
      const wrap = document.createElement('div');
      wrap.className = 'modal-backdrop open';
      wrap.innerHTML = `
        <div class="modal sm">
          <div class="modal-body confirm-box">
            <div class="ico">&#9888;&#65039;</div>
            <h3>${escapeHtml(title)}</h3>
            <p>${escapeHtml(text)}</p>
          </div>
          <div class="modal-foot">
            <button class="btn ghost" data-no>Cancel</button>
            <button class="btn danger" data-yes>Delete</button>
          </div>
        </div>`;
      document.body.appendChild(wrap);
      wrap.querySelector('[data-no]').onclick = () => { wrap.remove(); resolve(false); };
      wrap.querySelector('[data-yes]').onclick = () => { wrap.remove(); resolve(true); };
      wrap.addEventListener('click', (e) => { if (e.target === wrap) { wrap.remove(); resolve(false); } });
    });
  }

  function escapeHtml(s) {
    return String(s == null ? '' : s)
      .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
  }

  const FEED_LABELS = {
    govtjob: 'Govt Job',
    currentaffairs: 'Current Affairs',
    resultpost: 'Exam Result',
    dailynews: 'Daily News',
  };

  function fmtDate(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    if (isNaN(d)) return iso;
    return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  /** Inject the sidebar + topbar shell, highlight the active link, load the user. */
  async function shell(activePage, pageTitle, pageSub) {
    const nav = [
      { id: 'dashboard', href: 'dashboard.html', ico: '&#9636;', label: 'Dashboard' },
      { id: 'posts', href: 'posts.html', ico: '&#128196;', label: 'Posts' },
      { id: 'filters', href: 'filters.html', ico: '&#127991;', label: 'Filter Labels' },
      { id: 'notifications', href: 'notifications.html', ico: '&#128276;', label: 'Notifications' },
      { id: 'settings', href: 'settings.html', ico: '&#9881;', label: 'Settings' },
    ];
    const links = nav.map((n) => `
      <a href="${n.href}" class="${n.id === activePage ? 'active' : ''}">
        <span class="ico">${n.ico}</span>${n.label}
      </a>`).join('');

    const layout = document.createElement('div');
    layout.className = 'layout';
    layout.innerHTML = `
      <aside class="sidebar" id="sidebar">
        <div class="brand">
          <div class="logo">R1</div>
          <div class="name">RozgarOne<small>Admin Panel</small></div>
        </div>
        <nav class="nav">
          <div class="section-label">Manage</div>
          ${links}
        </nav>
        <div class="side-foot">RozgarOne v1.0</div>
      </aside>
      <div class="main">
        <header class="topbar">
          <div style="display:flex;align-items:center;gap:12px">
            <button class="burger" id="burger">&#9776;</button>
            <div class="page-title">
              <h2>${escapeHtml(pageTitle)}</h2>
              <span>${escapeHtml(pageSub || '')}</span>
            </div>
          </div>
          <div class="top-user">
            <div class="who"><b id="whoName">Admin</b><small>Signed in</small></div>
            <div class="avatar" id="whoAvatar">A</div>
            <button class="btn ghost sm" id="logoutBtn">Logout</button>
          </div>
        </header>
        <main class="content" id="page"></main>
      </div>`;

    document.body.innerHTML = '';
    document.body.appendChild(layout);

    document.getElementById('burger').onclick = () => {
      document.getElementById('sidebar').classList.toggle('open');
    };
    document.getElementById('logoutBtn').onclick = async () => {
      try { await api.logout(); } catch (e) { /* ignore */ }
      sessionStorage.removeItem('csrf');
      location.href = 'index.html';
    };

    try {
      const me = await api.me();
      const name = (me.user && me.user.name) || 'Admin';
      document.getElementById('whoName').textContent = name;
      document.getElementById('whoAvatar').textContent = name.charAt(0).toUpperCase();
    } catch (e) { /* redirected */ }

    return document.getElementById('page');
  }

  window.RozgarAdmin = { api, toast, confirmDialog, escapeHtml, fmtDate, shell, FEED_LABELS, ApiError };
})();
