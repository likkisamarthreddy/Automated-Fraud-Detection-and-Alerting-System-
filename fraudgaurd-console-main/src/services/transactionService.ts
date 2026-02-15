import { apiClient } from "@/lib/api-client";

export interface Transaction {
    transactionId: string;
    userId: string;
    amount: number;
    currency: string;
    merchantId?: string;
    decision: "ALLOW" | "BLOCK" | "FLAG" | "ALERT";
    riskScore: number;
    reason?: string;
    createdAt: string; // ISO date string
    createdAt: string; // ISO date string
}

export interface DashboardMetrics {
    totalTransactions: number;
    approvedCount: number;
    flaggedCount: number;
    blockedCount: number;
    fraudRate: number;
}

export const transactionService = {
    getTransactions: async (params?: any) => {
        const response = await apiClient.get("/transactions", { params });
        return response.data.data; // Page<Transaction>
    },

    getMetrics: async (): Promise<DashboardMetrics> => {
        const response = await apiClient.get("/transactions/metrics");
        return response.data.data;
    }
};
