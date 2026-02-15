import { apiClient } from "@/lib/api-client";

export interface FraudRule {
    id?: number;
    name: string;
    description: string;
    condition: string;
    score: number;
    enabled: boolean;
    createdAt?: string;
}

export const ruleService = {
    getRules: async (): Promise<FraudRule[]> => {
        const response = await apiClient.get("/rules");
        // FraudRuleController does NOT use ApiResponse wrapper
        return response.data;
    },

    getEnabledRules: async (): Promise<FraudRule[]> => {
        const response = await apiClient.get("/rules/enabled");
        return response.data;
    },

    getRuleById: async (id: number): Promise<FraudRule> => {
        const response = await apiClient.get(`/rules/${id}`);
        return response.data;
    },

    createRule: async (rule: FraudRule): Promise<FraudRule> => {
        const response = await apiClient.post("/rules", rule);
        return response.data;
    },

    updateRule: async (id: number, rule: FraudRule): Promise<FraudRule> => {
        const response = await apiClient.put(`/rules/${id}`, rule);
        return response.data;
    },

    deleteRule: async (id: number): Promise<void> => {
        await apiClient.delete(`/rules/${id}`);
    }
};
