import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { TrendingUp, TrendingDown, ShieldAlert, Ban, Activity, AlertTriangle } from "lucide-react";
import { transactionService, DashboardMetrics, Transaction } from "@/services/transactionService";
import { alertService } from "@/services/alertService";

const useCountUp = (end: number, duration = 1500, delay = 0) => {
  const [count, setCount] = useState(0);
  useEffect(() => {
    const timeout = setTimeout(() => {
      const start = performance.now();
      const step = (now: number) => {
        const progress = Math.min((now - start) / duration, 1);
        const eased = 1 - Math.pow(1 - progress, 3);
        setCount(Math.floor(eased * end));
        if (progress < 1) requestAnimationFrame(step);
      };
      requestAnimationFrame(step);
    }, delay);
    return () => clearTimeout(timeout);
  }, [end, duration, delay]);
  return count;
};

const formatNumber = (n: number, template: string) => {
  if (template.includes(",")) return n.toLocaleString();
  if (template.includes("%")) return (n / 100).toFixed(2) + "%";
  return n.toString();
};

const CountUpValue = ({ value, template, delay }: { value: number; template: string; delay: number }) => {
  const count = useCountUp(value, 1500, delay);
  return <>{formatNumber(count, template)}</>;
};

import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell,
} from "recharts";

const trendData = Array.from({ length: 24 }, (_, i) => ({
  hour: `${i}:00`,
  fraud: Math.floor(Math.random() * 40 + 10 + Math.sin(i / 3) * 20),
  total: Math.floor(Math.random() * 500 + 800),
}));

/*
const decisionData = [
  { name: "Allowed", value: 78, color: "hsl(142, 71%, 45%)" },
  { name: "Alerted", value: 14, color: "hsl(38, 92%, 50%)" },
  { name: "Blocked", value: 8, color: "hsl(0, 84%, 60%)" },
];
*/

const statusCls: Record<string, string> = {
  BLOCK: "status-block",
  ALERT: "status-alert",
  ALLOW: "status-allow",
  FLAG: "status-alert"
};

const Dashboard = () => {
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);
  const [alertMetrics, setAlertMetrics] = useState<any>(null);
  const [recentTransactions, setRecentTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [txnMetrics, alrtMetrics, txns] = await Promise.all([
          transactionService.getMetrics(),
          alertService.getMetrics(),
          transactionService.getTransactions({ page: 0, size: 8 })
        ]);
        setMetrics(txnMetrics);
        setAlertMetrics(alrtMetrics);
        setRecentTransactions(txns.content || []); // backend returns Page object
      } catch (error) {
        console.error("Failed to fetch dashboard data", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const kpis = metrics ? [
    { label: "Total Transactions", value: metrics.totalTransactions, template: "1,234", change: "+0%", up: true, icon: Activity },
    { label: "Fraud Rate", value: metrics.fraudRate * 100, template: "0.00%", change: "0%", up: false, icon: ShieldAlert }, // backend returns 0-1 or 0-100? Assuming 0-1 based on mockup needing %
    { label: "Blocked Attempts", value: metrics.blockedCount, template: "1,234", change: "+0%", up: true, icon: Ban },
    { label: "Active Alerts", value: alertMetrics?.openAlerts || 0, template: "123", change: "0", up: true, icon: AlertTriangle },
  ] : [];

  const decisionData = metrics ? [
    { name: "Allowed", value: metrics.approvedCount, color: "hsl(142, 71%, 45%)" },
    { name: "Alerted", value: metrics.flaggedCount, color: "hsl(38, 92%, 50%)" },
    { name: "Blocked", value: metrics.blockedCount, color: "hsl(0, 84%, 60%)" },
  ] : [];

  if (loading) return <div className="p-8 text-center">Loading dashboard...</div>;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Dashboard</h1>
        <p className="text-sm text-muted-foreground">Executive fraud intelligence overview</p>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {kpis.map((kpi, i) => (
          <motion.div
            key={i}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.1 }}
            className="glass-card-static p-5 space-y-3"
          >
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">{kpi.label}</span>
              <kpi.icon className="h-4 w-4 text-muted-foreground" />
            </div>
            <div className="flex items-end gap-2">
              <span className="text-2xl font-bold">
                <CountUpValue value={kpi.value} template={kpi.template} delay={i * 150} />
              </span>
              {/* Change indicator removed/static as backend doesn't provide history yet */}
            </div>
          </motion.div>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 glass-card-static p-6 flex flex-col justify-center items-center text-center space-y-4">
          <div className="relative">
            <div className="absolute inset-0 bg-primary/20 blur-xl rounded-full animate-pulse" />
            <Activity className="h-12 w-12 text-primary relative z-10" />
          </div>
          <div>
            <h3 className="text-lg font-semibold">Real-Time Monitoring Active</h3>
            <p className="text-sm text-muted-foreground max-w-sm mx-auto mt-2">
              The system is currently analyzing transactions in real-time.
              Historical trend analysis will populate as data accumulates.
            </p>
          </div>
          <div className="flex gap-4 text-xs font-mono text-muted-foreground mt-4">
            <span className="flex items-center gap-2"><span className="w-2 h-2 rounded-full bg-success animate-pulse" /> Engine Online</span>
            <span className="flex items-center gap-2"><span className="w-2 h-2 rounded-full bg-primary animate-pulse" /> Ingesting Events</span>
          </div>
        </div>

        {/* Decision Distribution */}
        <div className="glass-card-static p-6">
          <h3 className="text-sm font-semibold mb-4">Decision Distribution</h3>
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie data={decisionData} cx="50%" cy="50%" innerRadius={55} outerRadius={80} dataKey="value" strokeWidth={0}>
                {decisionData.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Pie>
            </PieChart>
          </ResponsiveContainer>
          <div className="flex justify-center gap-4 mt-2">
            {decisionData.map((d, i) => (
              <div key={i} className="flex items-center gap-1.5 text-xs">
                <div className="w-2.5 h-2.5 rounded-full" style={{ background: d.color }} />
                <span className="text-muted-foreground">{d.name} {d.value}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Live Feed */}
      <div className="glass-card-static p-6">
        <h3 className="text-sm font-semibold mb-4">Recent Transactions</h3>
        <div className="space-y-2 max-h-[300px] overflow-y-auto">
          {recentTransactions.map((item, i) => (
            <motion.div
              key={item.transactionId || i}
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: i * 0.08 }}
              className="flex items-center justify-between p-3 rounded-xl bg-accent/20 hover:bg-accent/40 transition-colors"
            >
              <div className="flex items-center gap-3">
                <span className={statusCls[item.decision] || "status-allow"}>{item.decision}</span>
                <span className="text-sm font-mono">{item.userId}</span>
                <span className="text-sm text-muted-foreground hidden sm:inline">— ₹{item.amount.toLocaleString()} ({item.merchantId || "Unknown"})</span>
              </div>
              <span className="text-xs text-muted-foreground">{new Date(item.createdAt).toLocaleTimeString()}</span>
            </motion.div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
