import { apiClient } from "@/lib/api-client";

export interface Alert {
    alertId: number;
    transactionId: string;
    userId: string;
    amount: number;
    riskScore: number;
    severity: string;
    reason: string;
    status: string; // Supports OPEN, RESOLVED, BLOCKED, ALLOWED, UNDER_REVIEW
    createdAt: string;
}

export interface AlertMetrics {
    totalAlerts: number;
    openAlerts: number;
    resolvedAlerts: number; // For backward compatibility, now means all closed alerts
}

export const alertService = {
    getAlerts: async (params?: any) => {
        const response = await apiClient.get('/alerts', { params });
        return response.data.data;
    },
    getMetrics: async () => {
        const response = await apiClient.get('/alerts/metrics');
        return response.data.data;
    },
    blockAlert: async (id: number) => {
        const response = await apiClient.put(`/alerts/${id}/block`);
        return response.data.data;
    },
    allowAlert: async (id: number) => {
        const response = await apiClient.put(`/alerts/${id}/allow`);
        return response.data.data;
    },
    reviewAlert: async (id: number) => {
        const response = await apiClient.put(`/alerts/${id}/review`);
        return response.data.data;
    }
};
