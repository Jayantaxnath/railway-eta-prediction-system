import { useEffect, useRef } from "react";

export function useAutoRefresh(callback, intervalMs = 30000, enabled = false) {
  const savedCallback = useRef(callback);

  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect(() => {
    if (!enabled || !intervalMs) return;

    const id = setInterval(() => {
      savedCallback.current();
    }, intervalMs);

    return () => clearInterval(id);
  }, [intervalMs, enabled]);
}
