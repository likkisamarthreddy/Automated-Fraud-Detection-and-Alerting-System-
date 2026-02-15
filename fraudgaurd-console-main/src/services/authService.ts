import { apiClient } from "@/lib/api-client";

export interface User {
    username: string;
    role: string;
    token: string;
}

export const authService = {
    login: async (username: string, password: string): Promise<User> => {
        const response = await apiClient.post("/auth/login", { username, password });
        // Based on AuthController: ApiResponse<AuthResponse> where AuthResponse has token and user
        // The previous analysis showed:
        // return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        // AuthResponse likely contains { token, user: { username, role } }
        // Let's assume response.data is ApiResponse
        const data = response.data.data;
        if (data.token) {
            localStorage.setItem("authToken", data.token);
            localStorage.setItem("user", JSON.stringify(data));
        }
        return data;
    },

    register: async (username: string, password: string) => {
        const response = await apiClient.post("/auth/register", { username, password });
        return response.data;
    },

    logout: () => {
        localStorage.removeItem("authToken");
        localStorage.removeItem("user");
    }
};
