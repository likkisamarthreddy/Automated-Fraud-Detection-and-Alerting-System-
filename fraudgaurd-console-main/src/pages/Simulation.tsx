import { useState } from "react";
import { motion } from "framer-motion";
import { Send, Smartphone, Globe, CreditCard } from "lucide-react";
import { apiClient } from "@/lib/api-client";
import { toast } from "sonner";

const Simulation = () => {
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        userId: "USR-SIM-01",
        amount: 5000,
        currency: "INR",
        merchantId: "AMAZON-IN",
        device: "Mobile-iPhone13",
        ipAddress: "192.168.1.100"
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSimulate = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            await apiClient.post("/transactions", formData);
            toast.success("Transaction sent successfully!");
            // Optionally reset or keep for rapid fire
        } catch (error) {
            console.error("Simulation failed", error);
            toast.error("Failed to send transaction");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="space-y-6 max-w-2xl mx-auto">
            <div>
                <h1 className="text-2xl font-bold">Transaction Simulator</h1>
                <p className="text-sm text-muted-foreground">Manually generate transactions to test fraud rules</p>
            </div>

            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="glass-card-static p-8"
            >
                <form onSubmit={handleSimulate} className="space-y-6">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="space-y-2">
                            <label className="text-sm font-medium">User ID</label>
                            <input
                                name="userId"
                                value={formData.userId}
                                onChange={handleChange}
                                className="w-full px-3 py-2 rounded-lg bg-accent/30 border border-border focus:ring-2 focus:ring-primary/50 outline-none"
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-medium">Amount (₹)</label>
                            <input
                                name="amount"
                                type="number"
                                value={formData.amount}
                                onChange={handleChange}
                                className="w-full px-3 py-2 rounded-lg bg-accent/30 border border-border focus:ring-2 focus:ring-primary/50 outline-none font-mono"
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-medium">Merchant</label>
                            <div className="relative">
                                <Globe className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
                                <input
                                    name="merchantId"
                                    value={formData.merchantId}
                                    onChange={handleChange}
                                    className="w-full pl-9 pr-3 py-2 rounded-lg bg-accent/30 border border-border focus:ring-2 focus:ring-primary/50 outline-none"
                                    required
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-medium">Device ID</label>
                            <div className="relative">
                                <Smartphone className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
                                <input
                                    name="device"
                                    value={formData.device}
                                    onChange={handleChange}
                                    className="w-full pl-9 pr-3 py-2 rounded-lg bg-accent/30 border border-border focus:ring-2 focus:ring-primary/50 outline-none"
                                    required
                                />
                            </div>
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-primary text-primary-foreground py-3 rounded-xl font-medium hover:opacity-90 transition-all orange-glow flex items-center justify-center gap-2"
                    >
                        {loading ? (
                            <span className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                        ) : (
                            <>
                                <Send className="w-4 h-4" /> Simulate Transaction
                            </>
                        )}
                    </button>
                </form>
            </motion.div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {[
                    { label: "Small Purchase", amt: 1500, user: "USR-SIM-02", color: "bg-success/10 text-success" },
                    { label: "High Value", amt: 65000, user: "USR-SIM-03", color: "bg-warning/10 text-warning" },
                    { label: "Extreme Value", amt: 150000, user: "USR-SIM-04", color: "bg-destructive/10 text-destructive" }
                ].map((preset, i) => (
                    <button
                        key={i}
                        type="button"
                        onClick={() => setFormData({ ...formData, amount: preset.amt, userId: preset.user })}
                        className={`p-4 rounded-xl border border-border/50 hover:border-primary/50 transition-all text-left space-y-1 ${preset.color}`}
                    >
                        <div className="font-bold text-sm">{preset.label}</div>
                        <div className="text-xs opacity-80">Sets ₹{preset.amt.toLocaleString()}</div>
                    </button>
                ))}
            </div>
        </div>
    );
};

export default Simulation;
