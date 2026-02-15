import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { AlertTriangle, MessageSquare, UserCheck, Clock, CheckCircle } from "lucide-react";
import { alertService, Alert, AlertMetrics } from "@/services/alertService";
import { toast } from "sonner";

const sevCls: Record<string, string> = {
  CRITICAL: "status-block",
  HIGH: "status-block",
  MEDIUM: "status-alert",
  LOW: "status-info",
};

const statusStyles: Record<string, string> = {
  OPEN: "bg-primary/15 text-primary",
  ESCALATED: "bg-destructive/15 text-destructive animate-pulse",
  RESOLVED: "bg-success/15 text-success",
};

const Alerts = () => {
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [metrics, setMetrics] = useState<AlertMetrics | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchAlerts = async () => {
    try {
      const [alertsData, metricsData] = await Promise.all([
        alertService.getAlerts({ size: 50 }),
        alertService.getMetrics(),
      ]);
      setAlerts(alertsData.content || []);
      setMetrics(metricsData);
    } catch (error) {
      console.error("Failed to fetch alerts", error);
      toast.error("Failed to load alerts");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAlerts();
  }, []);

  const handleResolve = async (id: number) => {
    try {
      await alertService.resolveAlert(id);
      toast.success("Alert resolved successfully");
      fetchAlerts(); // Refresh data
    } catch (error) {
      console.error("Failed to resolve alert", error);
      toast.error("Failed to resolve alert");
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Alerts</h1>
        <p className="text-sm text-muted-foreground">Case management and fraud alert triage</p>
      </div>

      {/* Summary */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {[
          { label: "Open", count: metrics?.openAlerts || 0, icon: AlertTriangle, cls: "text-primary" },
          { label: "Resolved", count: metrics?.resolvedAlerts || 0, icon: UserCheck, cls: "text-success" },
          { label: "Total", count: (metrics?.openAlerts || 0) + (metrics?.resolvedAlerts || 0), icon: Clock, cls: "text-info" },
        ].map((s, i) => (
          <motion.div
            key={i}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.1 }}
            className="glass-card-static p-5 flex items-center gap-4"
          >
            <s.icon className={`h-5 w-5 ${s.cls}`} />
            <div>
              <span className="text-2xl font-bold">{s.count}</span>
              <p className="text-xs text-muted-foreground">{s.label}</p>
            </div>
          </motion.div>
        ))}
      </div>

      {/* Alerts Table */}
      <div className="glass-card-static overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border/50">
                {["Alert ID", "Transaction", "Reason", "Severity", "Status", "Time", "Actions"].map((h) => (
                  <th key={h} className="text-left py-3 px-4 text-xs font-medium text-muted-foreground uppercase tracking-wider">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan={7} className="text-center py-8">Loading alerts...</td></tr>
              ) : alerts.length === 0 ? (
                <tr><td colSpan={7} className="text-center py-8 text-muted-foreground">No alerts found</td></tr>
              ) : alerts.map((alert, i) => (
                <motion.tr
                  key={alert.alertId}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: i * 0.05 }}
                  className="border-b border-border/30 hover:bg-accent/20 transition-colors"
                >
                  <td className="py-3 px-4 font-mono text-xs">ALR-{alert.alertId}</td>
                  <td className="py-3 px-4 font-mono text-xs text-muted-foreground">{alert.transactionId}</td>
                  <td className="py-3 px-4">{alert.reason}</td>
                  <td className="py-3 px-4"><span className={sevCls[alert.severity]}>{alert.severity}</span></td>
                  <td className="py-3 px-4">
                    <span className={`text-xs font-semibold px-2.5 py-0.5 rounded-full ${statusStyles[alert.status]}`}>
                      {alert.status}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-xs text-muted-foreground">{new Date(alert.createdAt).toLocaleString()}</td>
                  <td className="py-3 px-4">
                    {alert.status === "OPEN" && (
                      <button
                        onClick={() => handleResolve(alert.alertId)}
                        className="text-success hover:text-success/80 transition-colors"
                        title="Resolve Alert"
                      >
                        <CheckCircle className="h-5 w-5" />
                      </button>
                    )}
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Alerts;
