// Empty = same origin: the browser calls /api/... on whatever host served the page,
// and the Vite dev server proxies it to the backend (see vite.config.js). This works
// from any device (phone on the LAN, etc.). Set VITE_API_BASE_URL only when the
// backend lives on a different host than the frontend.
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "";

export const API_TIMEOUT = 30000;
