import { Header } from "../components/common/Header";
import { Footer } from "../components/common/Footer";
import { HeroSection } from "../components/dashboard/HeroSection";
import { RecentSearches } from "../components/dashboard/RecentSearches";

export function DashboardPage({
  trainNumber,
  setTrainNumber,
  onSearch,
  loading,
  error,
  clearError,
  recentSearches,
  onRemoveRecentSearch,
  onClearAllRecentSearches,
  onNavigateView,
}) {
  return (
    <div className="railx-app">
      <Header
        variant="main"
        onNavigateHome={() => onNavigateView("dashboard")}
        onNavigateView={onNavigateView}
        activeView="passenger"
      />

      <main>
        <HeroSection
          trainNumber={trainNumber}
          setTrainNumber={setTrainNumber}
          onSearch={onSearch}
          loading={loading}
          error={error}
          clearError={clearError}
        />

        <RecentSearches
          recentSearches={recentSearches}
          onSelectSearch={(num) => {
            setTrainNumber(num);
            onSearch(num);
          }}
          onRemoveSearch={onRemoveRecentSearch}
          onClearAll={onClearAllRecentSearches}
        />
      </main>

      <Footer />
    </div>
  );
}
