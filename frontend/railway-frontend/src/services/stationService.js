import apiClient from "./api";

export const stationService = {
  /**
   * Get all registered stations
   */
  async getAllStations() {
    return apiClient.get("/api/stations");
  },

  /**
   * Get details for a single station
   * @param {string} stationCode
   */
  async getStationByCode(stationCode) {
    return apiClient.get(`/api/stations/${stationCode}`);
  },

  /**
   * Get live arrival & departure board for platform display
   * @param {string} stationCode
   * @param {number} hours
   */
  async getLiveStationBoard(stationCode, hours = 4) {
    return apiClient.get(`/api/stations/${stationCode}/live`, {
      params: { hours },
    });
  },

  /**
   * Autocomplete search for stations by code, name, or city
   * @param {string} query
   */
  async searchStations(query) {
    return apiClient.get("/api/stations/search", { params: { q: query } });
  },
};
