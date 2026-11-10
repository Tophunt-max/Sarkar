#!/usr/bin/env node
/**
 * Dev/demo server: runs the identical RozgarOne API contract without PHP/MySQL.
 * Serves the public api/, the admin API and the admin panel as static files.
 *
 *   node server.js            → http://localhost:8000
 *
 * Data is persisted to data/db.json (seeded on first run).
 */
'use strict';

const http = require('http');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const ROOT = __dirname;
const ADMIN_DIR = path.join(ROOT, '..', 'admin');
const DATA_DIR = path.join(ROOT, 'data');
const DB_FILE = path.join(DATA_DIR, 'db.json');
const PORT = Number(process.env.PORT || 8000);

const FEEDS = ['govtjob', 'currentaffairs', 'resultpost', 'dailynews'];
const GROUPS = ['Category', 'Profession', 'Qualification', 'Location'];

// ---------------------------------------------------------------- persistence
function seed() {
  const today = new Date();
  const d = (offset) => new Date(today.getTime() - offset * 864e5).toISOString().slice(0, 10);
  const fmt = (iso) => new Date(iso).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
  const rows = [
    ['govtjob', 'UPSC ESE (Preliminary) Exam 2024 — 247 Vacancies', 'Union Public Service Commission invites online applications for Engineering Services Examination 2024. Total 247 posts across Civil, Mechanical, Electrical and Electronics branches. <b>Eligibility:</b> B.E / B.Tech or equivalent. <b>Age:</b> 21–30 years. <b>Application fee:</b> ₹200 (exempted for SC/ST/PwD/Female).', 1, 0],
    ['govtjob', 'SSC CGL 2024 — 17,727 Posts Notification Out', 'Staff Selection Commission has released the Combined Graduate Level Examination 2024 notification for 17,727 vacancies in Group B and C posts. Graduation in any discipline required. Tier-1 exam will be held in September 2024.', 2, 0],
    ['govtjob', 'RRB NTPC 2024 — 11,558 Vacancies for Graduates', 'Railway Recruitment Board invites online applications for Non-Technical Popular Categories. Graduate and undergraduate posts available across all zones. Apply before 28 July 2024.', 3, 0],
    ['govtjob', 'IBPS PO XV Recruitment — 4,455 Vacancies', 'Institute of Banking Personnel Selection announces Probationary Officer recruitment for 2024–25. Any graduate may apply. Preliminary exam in October 2024.', 4, 1],
    ['currentaffairs', 'Daily Current Affairs — 09 November', 'National: The Union Cabinet approved the PM Vishwakarma scheme extension. International: India and UAE signed a bilateral investment treaty. Sports: India won the ICC T20 series 3-1. Awards: Dr. R. Chidambaram received the Lifetime Achievement Award in Science.', 1, 0],
    ['currentaffairs', 'Current Affairs Quiz — Important Days in November', "November 14 is observed as World Diabetes Day and Children's Day in India. November 19 marks World Toilet Day. National Education Day is remembered on November 11.", 2, 0],
    ['currentaffairs', 'Weekly Current Affairs Capsule — Economy', "RBI kept the repo rate unchanged at 6.5%. India's GDP grew by 7.8% in Q1 FY25. Forex reserves reached USD 675 billion. Inflation eased to 4.3% in September.", 3, 0],
    ['resultpost', 'SSC CHSL 2024 Tier-I Result Declared', 'The Staff Selection Commission has declared the Tier-I result for Combined Higher Secondary Level Examination 2024. Candidates can check their marks using their roll number.', 1, 0],
    ['resultpost', 'UPSC Civil Services Final Result 2024', 'The Union Public Service Commission has announced the final result of Civil Service Examination 2024. A total of 1,016 candidates have been recommended.', 2, 0],
    ['dailynews', "Today's Newspaper Headlines — 09 November", 'Top stories: New rail corridor approved in the North-East, ISRO announces Gaganyaan test flight window, RBI issues digital lending guidelines.', 1, 0],
    ['dailynews', 'Employment News Weekly Highlights', 'This week: 12,000+ vacancies in state PSUs, teaching faculty recruitment in central universities, and apprenticeship openings in Indian Railways.', 2, 0],
  ];
  let id = 0;
  const posts = rows.map(([feed, title, content, off, expired]) => {
    id += 1;
    return {
      id,
      postid: 'SRK' + String(id).padStart(5, '0'),
      feed,
      jobTitle: title,
      jobContent: content,
      postPublishDate: d(off),
      formattedDate: fmt(d(off)),
      expiredDate: feed === 'govtjob' ? fmt(d(off - 20)) : '',
      isExpired: expired,
      isDeleted: 0,
      updatedAt: new Date().toISOString(),
    };
  });

  const filters = [];
  let fid = 0;
  const fData = {
    Category: ['Banking & Insurance', 'Railway', 'Defence', 'Teaching'],
    Profession: ['Engineering', 'Medical', 'Law'],
    Qualification: ['10th Pass', '12th Pass', 'Graduate', 'Post Graduate'],
    Location: ['Delhi', 'Maharashtra', 'Karnataka', 'All India'],
  };
  Object.entries(fData).forEach(([g, list]) => list.forEach((n) => filters.push({ id: ++fid, group_name: g, category_name: n })));

  return {
    posts,
    filters,
    notifications: [
      { id: 1, maintitle: 'SSC CGL 2024 notification released', secondtitle: 'Apply online before 24 July 2024', publishdate: d(0) },
      { id: 2, maintitle: 'UPSC ESE admit card available', secondtitle: 'Download from the official website', publishdate: d(2) },
      { id: 3, maintitle: 'New railway vacancy announced', secondtitle: '11,558 posts across all zones', publishdate: d(5) },
    ],
    settings: {
      admob_banner: 'ca-app-pub-3940256099942544/6300978111',
      admob_interstitial: 'ca-app-pub-3940256099942544/1033173712',
      admob_native: 'ca-app-pub-3940256099942544/2247696110',
      admob_appopen: 'ca-app-pub-3940256099942544/9257395921',
    },
    admin: { username: 'admin', password: 'admin123', name: 'Administrator' },
    sessions: {},
  };
}

function loadDb() {
  if (!fs.existsSync(DB_FILE)) {
    fs.mkdirSync(DATA_DIR, { recursive: true });
    const db = seed();
    fs.writeFileSync(DB_FILE, JSON.stringify(db, null, 2));
    return db;
  }
  return JSON.parse(fs.readFileSync(DB_FILE, 'utf8'));
}

let db = loadDb();
const save = () => fs.writeFileSync(DB_FILE, JSON.stringify(db, null, 2));
const nextId = (list) => (list.reduce((m, r) => Math.max(m, r.id), 0) + 1);

// ---------------------------------------------------------------- helpers
function send(res, code, body, type = 'application/json; charset=utf-8') {
  const payload = typeof body === 'string' ? body : JSON.stringify(body);
  res.writeHead(code, {
    'Content-Type': type,
    'Content-Length': Buffer.byteLength(payload),
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': 'Content-Type, X-CSRF-Token',
    'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
    'Cache-Control': 'no-store',
  });
  res.end(payload);
}
const ok = (res, key, rows) => send(res, 200, { status: 'true', message: 'success', [key]: rows });
const fail = (res, message, code = 200) => send(res, code, { status: 'false', message });
const adminOk = (res, extra = {}) => send(res, 200, { status: 'true', ...extra });
const adminErr = (res, message, code = 400) => send(res, code, { status: 'false', message });

function readBody(req) {
  return new Promise((resolve) => {
    let raw = '';
    req.on('data', (c) => { raw += c; if (raw.length > 5e6) req.destroy(); });
    req.on('end', () => {
      if (!raw) return resolve({});
      try { resolve(JSON.parse(raw)); } catch { resolve(Object.fromEntries(new URLSearchParams(raw))); }
    });
  });
}

function parseCookies(req) {
  const out = {};
  (req.headers.cookie || '').split(';').forEach((p) => {
    const i = p.indexOf('=');
    if (i > 0) out[p.slice(0, i).trim()] = decodeURIComponent(p.slice(i + 1).trim());
  });
  return out;
}

function session(req) {
  const sid = parseCookies(req).rozgarone_admin;
  return sid && db.sessions[sid] ? { sid, ...db.sessions[sid] } : null;
}

const shapePost = (r, withExpired) => {
  const o = {
    jobTitle: r.jobTitle,
    jobContent: r.jobContent,
    postid: String(r.postid),
    postPublishDate: r.postPublishDate,
    formattedDate: r.formattedDate,
  };
  if (withExpired) o.expiredDate = r.expiredDate || '';
  return o;
};

const MIME = {
  '.html': 'text/html; charset=utf-8', '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8', '.json': 'application/json',
  '.svg': 'image/svg+xml', '.png': 'image/png', '.ico': 'image/x-icon',
  '.woff2': 'font/woff2', '.map': 'application/json',
};

function serveStatic(res, file) {
  if (!fs.existsSync(file) || fs.statSync(file).isDirectory()) return false;
  const ext = path.extname(file).toLowerCase();
  const body = fs.readFileSync(file);
  res.writeHead(200, { 'Content-Type': MIME[ext] || 'application/octet-stream', 'Content-Length': body.length });
  res.end(body);
  return true;
}

// ---------------------------------------------------------------- routes
async function handlePublic(url, req, res) {
  const ep = url.pathname.replace(/^\/api\//, '');
  const q = Object.fromEntries(url.searchParams);

  switch (ep) {
    case 'appsettings.php':
      return ok(res, 'appsettings', [{ ...db.settings }]);

    case 'getfilter.php': {
      const title = q.title || q.group || '';
      let list = db.filters;
      if (GROUPS.includes(title)) list = list.filter((f) => f.group_name === title);
      return ok(res, 'filter', list.map((f) => ({ category_name: f.category_name, id: String(f.id) })));
    }

    case 'getnotification.php':
      return ok(res, 'notification', [...db.notifications]
        .sort((a, b) => b.publishdate.localeCompare(a.publishdate) || b.id - a.id)
        .map((n) => ({ maintitle: n.maintitle, secondtitle: n.secondtitle, publishdate: n.publishdate })));

    case 'getpostbyfilter.php': {
      const body = req.method === 'POST' ? await readBody(req) : {};
      const p = { ...q, ...body };
      let list = db.posts.filter((x) => !x.isDeleted && x.feed === 'govtjob');
      const lbl = db.filters.find((f) => String(f.id) === String(p.labelid));
      if (!lbl && !p.labelid) return fail(res, 'labelid is required');
      if (lbl && p.filter && lbl.group_name !== p.filter) list = [];
      return ok(res, 'govtjob', list.sort((a, b) => b.postPublishDate.localeCompare(a.postPublishDate)).map((r) => shapePost(r, true)));
    }

    case 'getgovtjob.php':
      return ok(res, 'govtjob', db.posts.filter((x) => !x.isDeleted && x.feed === 'govtjob' && !x.isExpired)
        .sort((a, b) => b.postPublishDate.localeCompare(a.postPublishDate)).map((r) => shapePost(r, true)));

    case 'getexpiredpost.php':
      return ok(res, 'govtjob', db.posts.filter((x) => !x.isDeleted && x.feed === 'govtjob' && x.isExpired)
        .sort((a, b) => b.postPublishDate.localeCompare(a.postPublishDate)).map((r) => shapePost(r, true)));

    case 'getcurrentaffairs.php':
      return ok(res, 'currentaffairs', db.posts.filter((x) => !x.isDeleted && x.feed === 'currentaffairs')
        .sort((a, b) => b.postPublishDate.localeCompare(a.postPublishDate)).map((r) => shapePost(r, false)));

    case 'getresultpost.php':
      return ok(res, 'resultpost', db.posts.filter((x) => !x.isDeleted && x.feed === 'resultpost')
        .sort((a, b) => b.postPublishDate.localeCompare(a.postPublishDate)).map((r) => shapePost(r, false)));

    case 'getdailynews.php':
      return ok(res, 'dailynews', db.posts.filter((x) => !x.isDeleted && x.feed === 'dailynews')
        .sort((a, b) => b.postPublishDate.localeCompare(a.postPublishDate)).map((r) => shapePost(r, false)));

    default:
      return fail(res, 'Not found', 404);
  }
}

function toAdmin(r) {
  return {
    id: r.id, postid: r.postid, feed: r.feed, jobTitle: r.jobTitle, jobContent: r.jobContent,
    postPublishDate: r.postPublishDate, formattedDate: r.formattedDate,
    expiredDate: r.expiredDate || '', isExpired: r.isExpired, isDeleted: r.isDeleted, updatedAt: r.updatedAt,
  };
}

async function handleAdmin(url, req, res) {
  const ep = url.pathname.replace(/^\/admin_api\//, '').replace(/\.php$/, '');
  const q = Object.fromEntries(url.searchParams);
  const body = req.method === 'POST' ? await readBody(req) : {};
  const p = { ...q, ...body };
  const sess = session(req);

  if (ep === 'auth') {
    const action = p.action || 'me';
    if (action === 'login') {
      if (p.username !== db.admin.username || p.password !== db.admin.password) {
        return adminErr(res, 'Invalid credentials', 401);
      }
      const sid = crypto.randomBytes(16).toString('hex');
      db.sessions[sid] = { user: { username: db.admin.username, name: db.admin.name }, csrf: crypto.randomBytes(16).toString('hex') };
      save();
      res.setHeader('Set-Cookie', `rozgarone_admin=${sid}; Path=/; HttpOnly; SameSite=Lax`);
      return adminOk(res, { message: 'Logged in', user: db.sessions[sid].user, csrf: db.sessions[sid].csrf });
    }
    if (action === 'logout') {
      if (sess) { delete db.sessions[sess.sid]; save(); }
      res.setHeader('Set-Cookie', 'rozgarone_admin=; Path=/; Max-Age=0');
      return adminOk(res, { message: 'Logged out' });
    }
    if (!sess) return adminErr(res, 'Unauthorized', 401);
    return adminOk(res, { user: sess.user, csrf: sess.csrf });
  }

  if (!sess) return adminErr(res, 'Unauthorized', 401);

  if (ep === 'posts') {
    if (req.method === 'GET') {
      const feed = p.feed || '';
      const search = (p.search || '').toLowerCase();
      const scope = p.scope || 'active';
      const page = Math.max(1, Number(p.page) || 1);
      const per = Math.min(100, Math.max(5, Number(p.per_page) || 15));
      let list = db.posts.filter((x) => {
        if (FEEDS.includes(feed) && x.feed !== feed) return false;
        if (scope === 'expired' && !(x.isExpired && !x.isDeleted)) return false;
        if (scope === 'deleted' && !x.isDeleted) return false;
        if (scope !== 'deleted' && scope !== 'expired' && x.isDeleted) return false;
        if (search && !x.jobTitle.toLowerCase().includes(search)) return false;
        return true;
      }).sort((a, b) => b.postPublishDate.localeCompare(a.postPublishDate) || b.id - a.id);
      const total = list.length;
      const counts = Object.fromEntries(FEEDS.map((f) => [f, db.posts.filter((x) => x.feed === f && !x.isDeleted).length]));
      list = list.slice((page - 1) * per, page * per).map(toAdmin);
      return adminOk(res, { posts: list, total, page, per_page: per, feeds: counts });
    }
    if (p.action === 'delete' || p.action === 'restore') {
      const row = db.posts.find((x) => x.id === Number(p.id));
      if (!row) return adminErr(res, 'Post not found', 404);
      row.isDeleted = p.action === 'delete' ? 1 : 0;
      row.updatedAt = new Date().toISOString();
      save();
      return adminOk(res, { message: p.action === 'delete' ? 'Post deleted' : 'Post restored' });
    }
    const feed = p.feed;
    const title = (p.jobTitle || '').trim();
    if (!FEEDS.includes(feed)) return adminErr(res, 'Invalid feed');
    if (!title) return adminErr(res, 'jobTitle is required');
    const publish = (p.postPublishDate || '').slice(0, 10) || new Date().toISOString().slice(0, 10);
    const fmtDate = p.formattedDate || new Date(publish).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });

    if (p.action === 'update') {
      const row = db.posts.find((x) => x.id === Number(p.id));
      if (!row) return adminErr(res, 'Post not found', 404);
      Object.assign(row, {
        feed, jobTitle: title, jobContent: p.jobContent || '',
        postPublishDate: publish, formattedDate: fmtDate,
        expiredDate: p.expiredDate || '', isExpired: p.isExpired ? 1 : 0, isDeleted: 0,
        updatedAt: new Date().toISOString(),
      });
      save();
      return adminOk(res, { message: 'Post updated', id: row.id });
    }
    const row = {
      id: nextId(db.posts),
      postid: p.postid || 'SRK' + Date.now(),
      feed, jobTitle: title, jobContent: p.jobContent || '',
      postPublishDate: publish, formattedDate: fmtDate,
      expiredDate: p.expiredDate || '', isExpired: p.isExpired ? 1 : 0,
      isDeleted: 0, updatedAt: new Date().toISOString(),
    };
    db.posts.push(row);
    save();
    return adminOk(res, { message: 'Post created', id: row.id });
  }

  if (ep === 'filters') {
    if (req.method === 'GET') {
      let list = db.filters;
      if (GROUPS.includes(p.group)) list = list.filter((f) => f.group_name === p.group);
      return adminOk(res, { filters: list.map((f) => ({ ...f })) });
    }
    if (p.action === 'delete') {
      db.filters = db.filters.filter((f) => f.id !== Number(p.id));
      save();
      return adminOk(res, { message: 'Filter deleted' });
    }
    const group = p.group_name || p.group;
    const name = (p.category_name || p.name || '').trim();
    if (!GROUPS.includes(group)) return adminErr(res, 'Invalid group_name');
    if (!name) return adminErr(res, 'category_name is required');
    if (p.action === 'update') {
      const row = db.filters.find((f) => f.id === Number(p.id));
      if (!row) return adminErr(res, 'Filter not found', 404);
      row.group_name = group; row.category_name = name;
      save();
      return adminOk(res, { message: 'Filter updated', id: row.id });
    }
    const row = { id: nextId(db.filters), group_name: group, category_name: name };
    db.filters.push(row);
    save();
    return adminOk(res, { message: 'Filter created', id: row.id });
  }

  if (ep === 'notifications') {
    if (req.method === 'GET') {
      return adminOk(res, { notifications: [...db.notifications].sort((a, b) => b.publishdate.localeCompare(a.publishdate) || b.id - a.id) });
    }
    if (p.action === 'delete') {
      db.notifications = db.notifications.filter((n) => n.id !== Number(p.id));
      save();
      return adminOk(res, { message: 'Notification deleted' });
    }
    const main = (p.maintitle || '').trim();
    if (!main) return adminErr(res, 'maintitle is required');
    const data = { maintitle: main, secondtitle: p.secondtitle || '', publishdate: (p.publishdate || new Date().toISOString().slice(0, 10)).slice(0, 10) };
    if (p.action === 'update') {
      const row = db.notifications.find((n) => n.id === Number(p.id));
      if (!row) return adminErr(res, 'Notification not found', 404);
      Object.assign(row, data);
      save();
      return adminOk(res, { message: 'Notification updated', id: row.id });
    }
    const row = { id: nextId(db.notifications), ...data };
    db.notifications.push(row);
    save();
    return adminOk(res, { message: 'Notification created', id: row.id });
  }

  if (ep === 'settings') {
    if (req.method === 'GET') return adminOk(res, { settings: { ...db.settings } });
    if (p.action === 'password') {
      if (p.current_password !== db.admin.password) return adminErr(res, 'Current password is incorrect');
      if (!p.new_password || p.new_password.length < 6) return adminErr(res, 'New password must be at least 6 characters');
      db.admin.password = p.new_password;
      save();
      return adminOk(res, { message: 'Password updated' });
    }
    ['admob_banner', 'admob_interstitial', 'admob_native', 'admob_appopen'].forEach((k) => {
      if (p[k]) db.settings[k] = p[k];
    });
    save();
    return adminOk(res, { message: 'Settings saved' });
  }

  return adminErr(res, 'Not found', 404);
}

const server = http.createServer(async (req, res) => {
  if (req.method === 'OPTIONS') return send(res, 204, '');
  const url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  try {
    if (url.pathname.startsWith('/api/')) return await handlePublic(url, req, res);
    if (url.pathname.startsWith('/admin_api/')) return await handleAdmin(url, req, res);
    if (url.pathname === '/admin' || url.pathname.startsWith('/admin/')) {
      let rel = url.pathname.replace(/^\/admin\/?/, '') || 'index.html';
      if (!rel.includes('.')) rel = 'index.html';
      const file = path.join(ADMIN_DIR, rel);
      if (!file.startsWith(ADMIN_DIR)) return send(res, 403, 'Forbidden', 'text/plain');
      if (serveStatic(res, file)) return;
      return send(res, 404, 'Not found', 'text/plain');
    }
    if (url.pathname === '/') {
      res.writeHead(302, { Location: '/admin/' });
      return res.end();
    }
    return send(res, 404, 'Not found', 'text/plain');
  } catch (err) {
    console.error(err);
    return send(res, 500, { status: 'false', message: String(err.message || err) });
  }
});

server.listen(PORT, () => {
  console.log(`RozgarOne dev server → http://localhost:${PORT}`);
  console.log(`  Public API  : http://localhost:${PORT}/api/getgovtjob.php`);
  console.log(`  Admin panel : http://localhost:${PORT}/admin/   (admin / admin123)`);
});
