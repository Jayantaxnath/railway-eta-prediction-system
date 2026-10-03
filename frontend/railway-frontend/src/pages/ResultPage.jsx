import { Header } from "../components/common/Header";
import { Footer } from "../components/common/Footer";
import { Breadcrumb } from "../components/result/Breadcrumb";
import { TrainHeader } from "../components/result/TrainHeader";
import { DateSelector } from "../components/result/DateSelector";
import { StatusStrip } from "../components/result/StatusStrip";
import { RouteMapPanel } from "../components/result/RouteMapPanel";
import { StationTimelineTable } from "../components/result/StationTimelineTable";
import { EtaPredictionPanel } from "../components/result/EtaPredictionPanel";
import { PositionDetailsPanel } from "../components/result/PositionDetailsPanel";

export function ResultPage({
  trainData,
  liveData,
  etaData,
  featuresData,
  onRefresh,
  onTriggerPrediction,
  onApplySimulationImpact,
  onSelectDate,
  selectedDateIndex,
  predicting,
  onNavigateDashboard,
  onNavigateView,
}) {
  return (
    <div className="result-page">
      <Header
        onNavigateHome={onNavigateDashboard}
        onNavigateView={onNavigateView}
        activeView="passenger"
      />

      <main className="result-content">
        <Breadcrumb
          trainNumber={trainData?.trainNumber}
          trainName={trainData?.trainName}
          onNavigateHome={onNavigateDashboard}
        />

        <TrainHeader
          train={trainData}
          live={liveData}
          eta={etaData}
          onRefresh={onRefresh}
          onTriggerPrediction={onTriggerPrediction}
          predicting={predicting}
          onNewSearch={onNavigateDashboard}
        />

        <DateSelector
          onSelectDate={onSelectDate}
          selectedIndex={selectedDateIndex}
          runDays={trainData?.runDays}
        />

        <StatusStrip live={liveData} eta={etaData} />

        <RouteMapPanel
          trainNumber={trainData?.trainNumber}
          liveData={liveData}
          schedule={trainData?.schedule}
        />

        <StationTimelineTable live={liveData} train={trainData} eta={etaData} />

        <section className="bottom-panels">
          <EtaPredictionPanel eta={etaData} live={liveData} />
          <PositionDetailsPanel live={liveData} eta={etaData} />
        </section>
      </main>

      <Footer />
    </div>
  );
}
