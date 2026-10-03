/**
 * Calculates delay in minutes between scheduled HH:MM and expected HH:MM
 * @param {string} scheduled
 * @param {string} expected
 * @returns {number}
 */
export function calculateDelay(scheduled, expected) {
  if (!scheduled || !expected || scheduled === "--" || expected === "--") {
    return 0;
  }

  const [sh, sm] = scheduled.split(":").map(Number);
  const [eh, em] = expected.split(":").map(Number);

  if (Number.isNaN(sh) || Number.isNaN(sm) || Number.isNaN(eh) || Number.isNaN(em)) {
    return 0;
  }

  const scheduledMinutes = sh * 60 + sm;
  const expectedMinutes = eh * 60 + em;

  return Math.max(0, expectedMinutes - scheduledMinutes);
}

/**
 * Returns status metadata based on delay minutes
 * @param {number} delayMinutes
 * @returns {{ label: string, isLate: boolean }}
 */
export function getDelayStatus(delayMinutes) {
  const value = Number(delayMinutes);
  if (!Number.isFinite(value) || value <= 0) {
    return { label: "Running On Time", isLate: false };
  }
  return { label: `Delayed by ${value} mins`, isLate: true };
}
