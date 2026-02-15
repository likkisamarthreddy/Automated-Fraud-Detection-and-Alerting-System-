import { apiClient } from "@/lib/api-client";

export interface Alert {
    alertId: number;
    transactionId: string;
    reason: string;
    severity: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
    status: "OPEN" | "RESOLVED";
    createdAt: string;
}

export interface AlertMetrics {
    openAlerts: number;
    resolvedAlerts: number;
}

export const alertService = {
    getAlerts: async (params?: any) => {
        const response = await apiClient.get("/alerts", { params });
        return response.data.data; // Page<Alert>
    },

    resolveAlert: async (id: number) => {
        const response = await apiClient.put(`/alerts/${id}/resolve`);
        return response.data.data;
    },

    getMetrics: async (): Promise<AlertMetrics> => {
        const response = await apiClient.get("/alerts/metrics");
        return response.data.data;
    }
};
