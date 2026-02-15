import { useState } from "react";
import { Link, useLocation, Outlet } from "react-router-dom";
import {
  LayoutDashboard, BarChart3, CreditCard, AlertTriangle,
  Users, Settings, Activity, FileText, Shield,
  Bell, Menu, X, ChevronDown, LogOut, Send
} from "lucide-react";

const navItems = [
  { label: "Dashboard", icon: LayoutDashboard, path: "/dashboard" },
  { label: "Analytics", icon: BarChart3, path: "/analytics" },
  { label: "Transactions", icon: CreditCard, path: "/transactions" },
  { label: "Alerts", icon: AlertTriangle, path: "/alerts" },
  { label: "Simulate", icon: Send, path: "/simulate" },
  { label: "Risk Profiles", icon: Users, path: "/risk-profiles" },
  { label: "Rule Management", icon: Settings, path: "/rules" },
  { label: "System Health", icon: Activity, path: "/system-health" },
  { label: "Audit Logs", icon: FileText, path: "/audit-logs" },
];

const DashboardLayout = () => {
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [notifOpen, setNotifOpen] = useState(false);

  return (
    <div className="min-h-screen bg-background flex">
      {/* Sidebar */}
      <aside
        className={`fixed lg:static inset-y-0 left-0 z-40 flex flex-col border-r border-border/50 bg-sidebar transition-all duration-300 ${sidebarOpen ? "w-64" : "w-0 lg:w-16"
          } overflow-hidden`}
      >
        <div className="h-16 flex items-center gap-2 px-4 border-b border-border/50 shrink-0">
          <Shield className="h-6 w-6 text-primary shrink-0" />
          {sidebarOpen && <span className="font-bold text-lg tracking-tight">FraudGuard</span>}
        </div>

        <nav className="flex-1 py-4 px-2 space-y-1 overflow-y-auto">
          {navItems.map((item) => {
            const active = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${active
                  ? "bg-primary/10 text-primary border-l-2 border-primary"
                  : "text-muted-foreground hover:text-foreground hover:bg-accent/50"
                  }`}
              >
                <item.icon className="h-4 w-4 shrink-0" />
                {sidebarOpen && <span>{item.label}</span>}
              </Link>
            );
          })}
        </nav>

        <div className="p-4 border-t border-border/50">
          <button
            onClick={() => {
              // Clear token and redirect
              localStorage.removeItem("token");
              localStorage.removeItem("user");
              window.location.href = "/login";
            }}
            className="flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-destructive hover:bg-destructive/10 transition-all w-full"
          >
            <LogOut className="h-4 w-4 shrink-0" />
            {sidebarOpen && <span>Logout</span>}
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Header */}
        <header className="h-16 border-b border-border/50 flex items-center justify-between px-6 shrink-0 backdrop-blur-xl bg-background/80">
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="text-muted-foreground hover:text-foreground transition-colors"
          >
            {sidebarOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>

          <div className="flex items-center gap-4">
            {/* Notification bell */}
            <div className="relative">
              <button
                onClick={() => setNotifOpen(!notifOpen)}
                className="relative text-muted-foreground hover:text-foreground transition-colors"
              >
                <Bell className="h-5 w-5" />
                <span className="absolute -top-1 -right-1 w-4 h-4 rounded-full bg-destructive text-destructive-foreground text-[10px] font-bold flex items-center justify-center">
                  3
                </span>
              </button>

              {notifOpen && (
                <div className="absolute right-0 top-10 w-80 glass-card-static rounded-xl border border-border shadow-2xl p-4 space-y-3 z-50">
                  <h4 className="text-sm font-semibold">Notifications</h4>
                  {[
                    { text: "High-risk transaction blocked – ₹50,000", time: "2m ago", cls: "status-block" },
                    { text: "Velocity spike detected – USR-1023", time: "5m ago", cls: "status-alert" },
                    { text: "New device login – USR-7721", time: "12m ago", cls: "status-info" },
                  ].map((n, i) => (
                    <div key={i} className="flex items-start gap-3 p-2 rounded-lg hover:bg-accent/30 transition-colors">
                      <span className={n.cls}>!</span>
                      <div className="flex-1 min-w-0">
                        <p className="text-xs">{n.text}</p>
                        <p className="text-[10px] text-muted-foreground mt-0.5">{n.time}</p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Profile */}
            <div className="flex items-center gap-2 text-sm">
              <div className="w-8 h-8 rounded-full bg-primary/20 flex items-center justify-center text-primary font-semibold text-xs">
                AD
              </div>
              <span className="hidden md:block text-muted-foreground">Admin</span>
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;
