import { useMemo } from "react";

export function DateSelector({ onSelectDate, selectedIndex = 0, runDays = [] }) {
  const dates = useMemo(() => {
    const list = [];
    const today = new Date();
    const dayMap = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];

    let normRunDays = [];
    if (Array.isArray(runDays) && runDays.length > 0) {
      normRunDays = runDays.map((d) => String(d).toLowerCase().substring(0, 3));
    }

    let i = 0;
    let daysExamined = 0;
    // Walk back up to 30 days to find up to 6 valid run days
    while (list.length < 6 && daysExamined < 30) {
      const d = new Date(today);
      d.setDate(today.getDate() - i);
      daysExamined++;
      i++;

      const dayCode = dayMap[d.getDay()];
      if (normRunDays.length > 0 && !normRunDays.includes(dayCode)) {
        continue; // Train does not run on this day of week
      }

      const label = d.toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
      }).replace(/ /g, "-");

      // Friendlier chip text: "Today" / "Yesterday" / weekday, plus short date
      const dayTitle =
        i === 1
          ? "Today"
          : i === 2
          ? "Yesterday"
          : d.toLocaleDateString("en-IN", { weekday: "short" });
      const shortDate = d.toLocaleDateString("en-IN", { day: "numeric", month: "short" });

      const yyyy = d.getFullYear();
      const mm = String(d.getMonth() + 1).padStart(2, "0");
      const dd = String(d.getDate()).padStart(2, "0");
      const iso = `${yyyy}-${mm}-${dd}`;

      list.push({ label, iso, dayTitle, shortDate });
    }
    return list;
  }, [runDays]);

  const handleSelect = (item, index) => {
    if (onSelectDate) onSelectDate(item.iso, index, item.label);
  };

  return (
    <section className="date-section" aria-label="Journey start date">
      <div className="date-label">Journey start date</div>

      <div className="date-tabs" role="tablist">
        {dates.map((item, index) => (
          <button
            key={item.iso}
            type="button"
            role="tab"
            aria-selected={index === selectedIndex}
            aria-label={item.label}
            className={index === selectedIndex ? "date-tab active" : "date-tab"}
            onClick={() => handleSelect(item, index)}
          >
            <span className="date-tab-day">{item.dayTitle}</span>
            <span className="date-tab-date">{item.shortDate}</span>
          </button>
        ))}
      </div>
    </section>
  );
}



