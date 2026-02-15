import React from "react";
import { motion } from "framer-motion";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from "recharts";

const deviceData = [
  { name: "Desktop", count: 4500, risk: 22 },
  { name: "Mobile", count: 3200, risk: 35 },
  { name: "Tablet", count: 800, risk: 18 },
  { name: "Unknown", count: 320, risk: 72 },
];

const hourlyData = Array.from({ length: 24 }, (_, i) => ({
  hour: `${i}:00`,
  fraud: Math.floor(Math.random() * 50 + 5 + Math.sin(i / 4) * 25),
}));

const riskUsers = [
  { rank: 1, user: "USR-2847", avgRisk: 92, alerts: 14, velocity: 8 },
  { rank: 2, user: "USR-9100", avgRisk: 88, alerts: 11, velocity: 6 },
  { rank: 3, user: "USR-3301", avgRisk: 85, alerts: 9, velocity: 5 },
  { rank: 4, user: "USR-4022", avgRisk: 79, alerts: 7, velocity: 4 },
  { rank: 5, user: "USR-6655", avgRisk: 74, alerts: 6, velocity: 3 },
];

// Heatmap mock
const heatmapRows = 8;
const heatmapCols = 24;
const heatmapData = Array.from({ length: heatmapRows }, () =>
  Array.from({ length: heatmapCols }, () => Math.random())
);

const Analytics = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Analytics</h1>
        <p className="text-sm text-muted-foreground">Advanced fraud intelligence and risk analysis</p>
      </div>

      {/* Risk Heatmap */}
      <div className="glass-card-static p-6">
        <h3 className="text-sm font-semibold mb-4">Risk Heatmap (User × Time)</h3>
        <div className="overflow-x-auto">
          <div className="inline-grid gap-0.5" style={{ gridTemplateColumns: `80px repeat(${heatmapCols}, 1fr)` }}>
            <div />
            {Array.from({ length: heatmapCols }, (_, i) => (
              <div key={i} className="text-[9px] text-muted-foreground text-center">{i}h</div>
            ))}
            {heatmapData.map((row, ri) => (
              <React.Fragment key={`row-${ri}`}>
                <div className="text-xs text-muted-foreground flex items-center font-mono">
                  USR-{1000 + ri}
                </div>
                {row.map((val, ci) => (
                  <motion.div
                    key={`${ri}-${ci}`}
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: (ri * heatmapCols + ci) * 0.002 }}
                    className="w-full aspect-square rounded-sm"
                    style={{
                      background: val > 0.7
                        ? `hsl(0, 84%, ${60 - val * 20}%)`
                        : val > 0.4
                          ? `hsl(38, 92%, ${50 + (1 - val) * 20}%)`
                          : `hsl(142, 71%, ${45 + (1 - val) * 20}%)`,
                      opacity: 0.3 + val * 0.7,
                    }}
                    title={`Risk: ${Math.round(val * 100)}`}
                  />
                ))}
              </React.Fragment>
            ))}
          </div>
        </div>
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        {/* Device Risk */}
        <div className="glass-card-static p-6">
          <h3 className="text-sm font-semibold mb-4">Device Risk Breakdown</h3>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={deviceData}>
              <CartesianGrid strokeDasharray="3 3" stroke="hsl(215, 20%, 20%)" />
              <XAxis dataKey="name" tick={{ fontSize: 11, fill: "hsl(215, 20%, 65%)" }} tickLine={false} axisLine={false} />
              <YAxis tick={{ fontSize: 11, fill: "hsl(215, 20%, 65%)" }} tickLine={false} axisLine={false} />
              <Tooltip
                contentStyle={{
                  background: "hsl(217, 33%, 17%)",
                  border: "1px solid hsl(215, 20%, 20%)",
                  borderRadius: "12px",
                  fontSize: 12,
                }}
              />
              <Bar dataKey="count" fill="hsl(25, 95%, 53%)" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Fraud by Time */}
        <div className="glass-card-static p-6">
          <h3 className="text-sm font-semibold mb-4">Fraud by Time of Day</h3>
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={hourlyData}>
              <CartesianGrid strokeDasharray="3 3" stroke="hsl(215, 20%, 20%)" />
              <XAxis dataKey="hour" tick={{ fontSize: 11, fill: "hsl(215, 20%, 65%)" }} tickLine={false} axisLine={false} interval={3} />
              <YAxis tick={{ fontSize: 11, fill: "hsl(215, 20%, 65%)" }} tickLine={false} axisLine={false} />
              <Tooltip
                contentStyle={{
                  background: "hsl(217, 33%, 17%)",
                  border: "1px solid hsl(215, 20%, 20%)",
                  borderRadius: "12px",
                  fontSize: 12,
                }}
              />
              <Line type="monotone" dataKey="fraud" stroke="hsl(0, 84%, 60%)" strokeWidth={2} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* High-Risk Leaderboard */}
      <div className="glass-card-static p-6">
        <h3 className="text-sm font-semibold mb-4">High-Risk Users Leaderboard</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border/50">
                {["Rank", "User", "Avg Risk", "Alerts", "Velocity Flags"].map((h) => (
                  <th key={h} className="text-left py-3 px-4 text-xs font-medium text-muted-foreground uppercase tracking-wider">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {riskUsers.map((u, i) => (
                <motion.tr
                  key={u.rank}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: i * 0.08 }}
                  className="border-b border-border/30 hover:bg-accent/20 transition-colors"
                >
                  <td className="py-3 px-4 font-bold text-primary">#{u.rank}</td>
                  <td className="py-3 px-4 font-mono text-xs">{u.user}</td>
                  <td className="py-3 px-4">
                    <span className={u.avgRisk > 80 ? "text-destructive font-semibold" : "text-warning"}>{u.avgRisk}</span>
                  </td>
                  <td className="py-3 px-4">{u.alerts}</td>
                  <td className="py-3 px-4">{u.velocity}</td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Analytics;
