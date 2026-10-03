import { useState, useEffect, useRef } from "react";
import { useRecentSearches } from "./hooks/useRecentSearches";
import { useTrainTracker } from "./hooks/useTrainTracker";
import { DashboardPage } from "./pages/DashboardPage";
import { ResultPage } from "./pages/ResultPage";
import { ControlRoomPage } from "./pages/ControlRoomPage";
import { StationBoardPage } from "./pages/StationBoardPage";
import { LegalPage } from "./pages/LegalPage";
import "./App.css";

// Legal pages are addressable by URL hash (#privacy, #terms) so they can be linked/shared
const LEGAL_PAGES = { "#privacy": "privacy", "#terms": "terms" };
const legalPageFromHash = () => LEGAL_PAGES[window.location.hash] || null;
const isLegalPage = (p) => p === "privacy" || p === "terms";

function App() {
  const [page, setPage] = useState(() => legalPageFromHash() || "dashboard");
  const pageBeforeLegalRef = useRef("dashboard");

  const {
    recentSearches,
    saveRecentSearch,
    removeRecentSearch,
    clearAllRecentSearches,
  } = useRecentSearches();

  const handleSearchSuccess = (item) => {
    saveRecentSearch(item);
    setPage("result");
  };

  const {
    trainNumber,
    setTrainNumber,
    trainData,
    liveData,
    etaData,
    featuresData,
    selectedDateIndex,
    loading,
    predicting,
    error,
    clearError,
    fetchTrainStatus,
    changeStartDate,
    triggerLivePrediction,
    applySimulationImpact,
    refreshStatus,
  } = useTrainTracker(handleSearchSuccess);

  // Each view starts at the top, like a normal page navigation
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "instant" });
  }, [page]);

  // Footer links change the hash; the browser Back button clears it again
  useEffect(() => {
    const onHashChange = () => {
      const legal = legalPageFromHash();
      setPage((current) => {
        if (legal) {
          if (!isLegalPage(current)) pageBeforeLegalRef.current = current;
          return legal;
        }
        return isLegalPage(current) ? pageBeforeLegalRef.current : current;
      });
    };
    window.addEventListener("hashchange", onHashChange);
    return () => window.removeEventListener("hashchange", onHashChange);
  }, []);

  // Leaving a legal page through the app's own navigation drops the hash
  useEffect(() => {
    if (!isLegalPage(page) && legalPageFromHash()) {
      window.history.replaceState(null, "", window.location.pathname + window.location.search);
    }
  }, [page]);

  const handleNavigateView = (viewName) => {
    if (viewName === "dashboard" || viewName === "passenger") {
      if (trainData) {
        setPage("result");
      } else {
        setPage("dashboard");
      }
    } else {
      setPage(viewName);
    }
  };

  const handleSelectTrainFromPage = (num) => {
    setTrainNumber(num);
    fetchTrainStatus(num);
  };

  // Feedback while a train loads from pages that have no search button of their own
  const loadingIndicator = loading && (
    <>
      <div className="global-loading" role="progressbar" aria-label="Loading train" />
      {page !== "dashboard" && (
        <div className="loading-toast" role="status">
          Loading train {trainNumber}…
        </div>
      )}
    </>
  );

  let content;

  if (isLegalPage(page)) {
    content = (
      <LegalPage
        type={page}
        // Return to where the user came from (home for direct links)
        onBack={() => setPage(pageBeforeLegalRef.current)}
        onNavigateHome={() => setPage("dashboard")}
        onNavigateView={handleNavigateView}
      />
    );
  } else if (page === "controlroom") {
    content = (
      <ControlRoomPage
        onSelectTrain={handleSelectTrainFromPage}
        onNavigateHome={() => setPage("dashboard")}
        onNavigateView={handleNavigateView}
      />
    );
  } else if (page === "stationboard") {
    content = (
      <StationBoardPage
        onSelectTrain={handleSelectTrainFromPage}
        onNavigateHome={() => setPage("dashboard")}
        onNavigateView={handleNavigateView}
      />
    );
  } else if (page === "result" && trainData) {
    content = (
      <ResultPage
        trainData={trainData}
        liveData={liveData}
        etaData={etaData}
        featuresData={featuresData}
        onRefresh={refreshStatus}
        onTriggerPrediction={triggerLivePrediction}
        onApplySimulationImpact={applySimulationImpact}
        onSelectDate={changeStartDate}
        selectedDateIndex={selectedDateIndex}
        predicting={predicting}
        onNavigateDashboard={() => {
          setPage("dashboard");
          clearError();
        }}
        onNavigateView={handleNavigateView}
      />
    );
  } else {
    content = (
      <DashboardPage
        trainNumber={trainNumber}
        setTrainNumber={setTrainNumber}
        onSearch={(num) => fetchTrainStatus(num)}
        loading={loading}
        error={error}
        clearError={clearError}
        recentSearches={recentSearches}
        onRemoveRecentSearch={removeRecentSearch}
        onClearAllRecentSearches={clearAllRecentSearches}
        onNavigateView={handleNavigateView}
      />
    );
  }

  return (
    <>
      {loadingIndicator}
      {content}
    </>
  );
}

export default App;