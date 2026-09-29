package com.traineta.scheduler;

import com.traineta.service.EtaService;
import com.traineta.service.LiveTrainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TrainDataScheduler {

    private static final Logger log = LoggerFactory.getLogger(TrainDataScheduler.class);

    private final LiveTrainService liveTrainService;
    private final EtaService etaService;

    @Value("${train.ingestion.active-trains:12919,12920}")
    private String activeTrainNumbersStr;

    public TrainDataScheduler(LiveTrainService liveTrainService, EtaService etaService) {
        this.liveTrainService = liveTrainService;
        this.etaService = etaService;
    }

    @Scheduled(fixedRateString = "${train.ingestion.interval:300000}")
    public void fetchActiveTrainPositions() {
        log.info("Starting 5-minute scheduled live train data ingestion & ETA prediction pipeline...");
        List<String> activeTrains = Arrays.stream(activeTrainNumbersStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        for (String trainNumber : activeTrains) {
            try {
                log.info("1. Ingesting live data for active train: {}", trainNumber);
                liveTrainService.fetchAndSaveLiveStatus(trainNumber);

                log.info("2. Generating ETA prediction for active train: {}", trainNumber);
                etaService.generateAndSaveEta(trainNumber);

                log.info("Successfully completed automated cycle for train: {}", trainNumber);
            } catch (Exception e) {
                log.error("Error during automated scheduled pipeline for train {}: {}", trainNumber, e.getMessage(), e);
                // Continue to next train without throwing exception out of loop
            }
        }
        log.info("Finished 5-minute scheduled live train data ingestion & ETA prediction cycle.");
    }
}
