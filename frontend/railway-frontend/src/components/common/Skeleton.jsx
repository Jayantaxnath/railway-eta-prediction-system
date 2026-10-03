export function SkeletonBar({ width = "100%", height = 14, radius = 6, dark = false, style = {} }) {
  return (
    <div
      className={`skeleton${dark ? " skeleton-on-blue" : ""}`}
      style={{ width, height, borderRadius: radius, flexShrink: 0, ...style }}
    />
  );
}
