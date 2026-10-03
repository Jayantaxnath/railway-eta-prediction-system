import axios from "axios";
import { API_BASE_URL, API_TIMEOUT } from "../config/apiConfig";

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    let message = "Network error. Please check your connection.";
    if (error.response) {
      message = error.response.data?.message || `API error (${error.response.status})`;
    } else if (error.request) {
      message = "Unable to connect to the RailX backend.";
    }
    return Promise.reject(new Error(message));
  }
);

export default apiClient;
