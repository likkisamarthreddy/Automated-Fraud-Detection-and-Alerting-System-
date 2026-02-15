import { motion } from "framer-motion";
import { Activity, Database, Server, Cpu } from "lucide-react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";

const services = [
  { name: "API Gateway", status: "Healthy", icon: Server, uptime: "99.99%", latency: "12ms" },
  { name: "Kafka Cluster", status: "Healthy", icon: Activity, uptime: "99.97%", latency: "3ms" },
  { name: "Redis Cache", status: "Healthy", icon: Cpu, uptime: "99.99%", latency: "0.5ms" },
  { name: "MySQL Primary", status: "Degraded", icon: Database, uptime: "99.91%", latency: "45ms" },
];

const latencyData = Array.from({ length: 30 }, (_, i) => ({
  min: `${i}m`,
  api: Math.floor(Math.random() * 15 + 8),
  kafka: Math.floor(Math.random() * 5 + 1),
  redis: Math.random() * 1.5 + 0.2,
}));

const SystemHealth = () => (
  <div className="space-y-6">
    <div>
      <h1 className="text-2xl font-bold">System Health</h1>
      <p className="text-sm text-muted-foreground">Infrastructure monitoring and service status</p>
    </div>

    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {services.map((svc, i) => (
        <motion.div
          key={i}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: i * 0.1 }}
          className="glass-card-static p-5 space-y-3"
        >
          <div className="flex items-center justify-between">
            <svc.icon className="h-5 w-5 text-muted-foreground" />
            <span className={svc.status === "Healthy" ? "status-allow" : "status-alert"}>{svc.status}</span>
          </div>
          <h3 className="font-semibold">{svc.name}</h3>
          <div className="flex gap-4 text-xs text-muted-foreground">
            <span>Uptime: {svc.uptime}</span>
            <span>Latency: {svc.latency}</span>
          </div>
        </motion.div>
      ))}
    </div>

    <div className="glass-card-static p-6">
      <h3 className="text-sm font-semibold mb-4">Latency (30 min)</h3>
      <ResponsiveContainer width="100%" height={280}>
        <LineChart data={latencyData}>
          <CartesianGrid strokeDasharray="3 3" stroke="hsl(215, 20%, 20%)" />
          <XAxis dataKey="min" tick={{ fontSize: 11, fill: "hsl(215, 20%, 65%)" }} tickLine={false} axisLine={false} interval={4} />
          <YAxis tick={{ fontSize: 11, fill: "hsl(215, 20%, 65%)" }} tickLine={false} axisLine={false} />
          <Tooltip contentStyle={{ background: "hsl(217, 33%, 17%)", border: "1px solid hsl(215, 20%, 20%)", borderRadius: "12px", fontSize: 12 }} />
          <Line type="monotone" dataKey="api" stroke="hsl(25, 95%, 53%)" strokeWidth={2} dot={false} name="API" />
          <Line type="monotone" dataKey="kafka" stroke="hsl(217, 91%, 60%)" strokeWidth={2} dot={false} name="Kafka" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  </div>
);

export default SystemHealth;
