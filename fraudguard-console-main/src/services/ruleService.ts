import { apiClient } from "@/lib/api-client";

export interface FraudRule {
    id?: number;
    name: string;
    description: string;
    condition: string;
    score: number;
    enabled: boolean;
    createdAt?: string;
    
    // Original backend fields
    ruleId?: number;
    ruleName?: string;
    ruleType?: string;
    thresholdValue?: number;
    riskScoreWeight?: number;
}

const mapToFrontend = (data: any): FraudRule => ({
    id: data.ruleId,
    name: data.ruleName,
    description: data.description,
    condition: `${data.ruleType} > ${data.thresholdValue}`,
    score: data.riskScoreWeight,
    enabled: data.enabled,
    createdAt: data.createdAt,
    
    ruleId: data.ruleId,
    ruleName: data.ruleName,
    ruleType: data.ruleType,
    thresholdValue: data.thresholdValue,
    riskScoreWeight: data.riskScoreWeight
});

const mapToBackend = (rule: FraudRule): any => ({
    ruleId: rule.id,
    ruleName: rule.name,
    ruleType: rule.ruleType,
    enabled: rule.enabled,
    thresholdValue: rule.thresholdValue,
    riskScoreWeight: rule.score,
    description: rule.description
});

export const ruleService = {
    getRules: async (): Promise<FraudRule[]> => {
        const response = await apiClient.get("/rules");
        return response.data.map(mapToFrontend);
    },

    getEnabledRules: async (): Promise<FraudRule[]> => {
        const response = await apiClient.get("/rules/enabled");
        return response.data.map(mapToFrontend);
    },

    getRuleById: async (id: number): Promise<FraudRule> => {
        const response = await apiClient.get(`/rules/${id}`);
        return mapToFrontend(response.data);
    },

    createRule: async (rule: FraudRule): Promise<FraudRule> => {
        const response = await apiClient.post("/rules", mapToBackend(rule));
        return mapToFrontend(response.data);
    },

    updateRule: async (id: number, rule: FraudRule): Promise<FraudRule> => {
        const response = await apiClient.put(`/rules/${id}`, mapToBackend(rule));
        return mapToFrontend(response.data);
    },

    deleteRule: async (id: number): Promise<void> => {
        await apiClient.delete(`/rules/${id}`);
    }
};
