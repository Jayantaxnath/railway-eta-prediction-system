// In-memory cache shared across page mounts, so switching tabs (Train Status /
// Station Board / Control Room) reuses the last fetched result instead of
// re-hitting the API every time. Cleared only by an explicit refresh action,
// or on a full page reload.
const cache = new Map();

export function getCached(key) {
  return cache.get(key);
}

export function setCached(key, value) {
  cache.set(key, value);
  return value;
}

export function clearCached(key) {
  cache.delete(key);
}
