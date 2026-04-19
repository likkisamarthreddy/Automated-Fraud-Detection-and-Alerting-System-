import { motion } from "framer-motion";
import { Shield, Zap, BarChart3, ArrowRight, Activity, Lock, Globe } from "lucide-react";
import { Link } from "react-router-dom";

const fadeUp = {
  hidden: { opacity: 0, y: 30 },
  visible: (i: number) => ({
    opacity: 1,
    y: 0,
    transition: { delay: i * 0.15, duration: 0.6, ease: "easeOut" as const },
  }),
};

const Landing = () => {
  return (
    <div className="min-h-screen bg-background text-foreground overflow-hidden">
      {/* Nav */}
      <nav className="fixed top-0 w-full z-50 backdrop-blur-xl border-b border-border/50">
        <div className="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Shield className="h-7 w-7 text-primary" />
            <span className="text-xl font-bold tracking-tight">FraudGuard</span>
          </div>
          <div className="hidden md:flex items-center gap-8 text-sm text-muted-foreground">
            <a href="#features" className="hover:text-foreground transition-colors">Features</a>
            <a href="#how" className="hover:text-foreground transition-colors">How It Works</a>
            <Link to="/login" className="hover:text-foreground transition-colors">Sign In</Link>
            <Link
              to="/dashboard"
              className="bg-primary text-primary-foreground px-4 py-2 rounded-lg font-medium hover:opacity-90 transition-opacity orange-glow-sm"
            >
              Get Started
            </Link>
          </div>
        </div>
      </nav>

      {/* Hero */}
      <section className="relative pt-32 pb-20 px-6">
        {/* Animated blobs */}
        <div className="absolute top-20 left-1/4 w-72 h-72 bg-primary/20 rounded-full filter blur-[128px] animate-blob" />
        <div className="absolute top-40 right-1/4 w-96 h-96 bg-primary/10 rounded-full filter blur-[128px] animate-blob animation-delay-2000" />
        <div className="absolute bottom-0 left-1/2 w-80 h-80 bg-info/10 rounded-full filter blur-[128px] animate-blob animation-delay-4000" />

        <div className="max-w-7xl mx-auto relative z-10">
          <div className="grid lg:grid-cols-2 gap-16 items-center">
            <motion.div
              initial="hidden"
              animate="visible"
              className="space-y-8"
            >
              <motion.div variants={fadeUp} custom={0}>
                <span className="status-info text-sm">🔒 Enterprise-Grade Security</span>
              </motion.div>
              <motion.h1
                variants={fadeUp}
                custom={1}
                className="text-5xl md:text-6xl lg:text-7xl font-black leading-[1.05] tracking-tight"
              >
                Real-Time Fraud
                <br />
                Intelligence.
                <br />
                <span className="text-gradient-orange">Zero Compromise.</span>
              </motion.h1>
              <motion.p
                variants={fadeUp}
                custom={2}
                className="text-lg text-muted-foreground max-w-md"
              >
                Detect. Block. Analyze. Scale. — Protect every transaction with ML-powered fraud detection that never sleeps.
              </motion.p>
              <motion.div variants={fadeUp} custom={3} className="flex gap-4">
                <Link
                  to="/dashboard"
                  className="inline-flex items-center gap-2 bg-primary text-primary-foreground px-6 py-3 rounded-xl font-semibold hover:opacity-90 transition-all orange-glow"
                >
                  Get Started <ArrowRight className="h-4 w-4" />
                </Link>
                <Link
                  to="/dashboard"
                  className="inline-flex items-center gap-2 glass-card-static px-6 py-3 rounded-xl font-semibold hover:bg-accent transition-colors"
                >
                  View Demo
                </Link>
              </motion.div>
              <motion.div variants={fadeUp} custom={4} className="flex gap-8 pt-4 text-sm text-muted-foreground">
                <div><span className="text-foreground font-bold text-2xl">99.7%</span><br />Detection Rate</div>
                <div><span className="text-foreground font-bold text-2xl">&lt;50ms</span><br />Response Time</div>
                <div><span className="text-foreground font-bold text-2xl">10M+</span><br />Tx Processed</div>
              </motion.div>
            </motion.div>

            {/* Dashboard Mock */}
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.5, duration: 0.8 }}
              className="hidden lg:block"
            >
              <div className="glass-card-static p-6 space-y-4">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-medium">Fraud Detection Engine</span>
                  <span className="status-allow">ACTIVE</span>
                </div>
                {/* Mini chart mock */}
                <div className="h-32 relative overflow-hidden rounded-xl bg-accent/30">
                  <svg className="w-full h-full" viewBox="0 0 400 120">
                    <defs>
                      <linearGradient id="grad" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="hsl(25 95% 53%)" stopOpacity="0.4" />
                        <stop offset="100%" stopColor="hsl(25 95% 53%)" stopOpacity="0" />
                      </linearGradient>
                    </defs>
                    <path d="M0,80 Q50,60 100,65 T200,40 T300,55 T400,20" fill="none" stroke="hsl(25 95% 53%)" strokeWidth="2.5" />
                    <path d="M0,80 Q50,60 100,65 T200,40 T300,55 T400,20 V120 H0 Z" fill="url(#grad)" />
                  </svg>
                </div>
                {/* Mini alerts */}
                <div className="space-y-2">
                  {[
                    { status: "BLOCK", user: "USR-2847", amount: "₹50,000", cls: "status-block" },
                    { status: "ALERT", user: "USR-1023", amount: "Velocity spike", cls: "status-alert" },
                    { status: "ALLOW", user: "USR-4511", amount: "₹120", cls: "status-allow" },
                  ].map((item, i) => (
                    <motion.div
                      key={i}
                      initial={{ opacity: 0, x: 20 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{ delay: 1 + i * 0.2 }}
                      className="flex items-center justify-between p-2.5 rounded-lg bg-accent/20 text-sm"
                    >
                      <div className="flex items-center gap-2">
                        <span className={item.cls}>{item.status}</span>
                        <span className="text-muted-foreground">{item.user}</span>
                      </div>
                      <span className="font-mono text-xs text-muted-foreground">{item.amount}</span>
                    </motion.div>
                  ))}
                </div>
              </div>
            </motion.div>
          </div>
        </div>
      </section>

      {/* Features */}
      <section id="features" className="py-24 px-6">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-center mb-16"
          >
            <h2 className="text-3xl md:text-4xl font-bold mb-4">Built for Security Teams</h2>
            <p className="text-muted-foreground max-w-2xl mx-auto">
              Enterprise-grade fraud detection with real-time processing, intelligent ML models, and actionable analytics.
            </p>
          </motion.div>

          <div className="grid md:grid-cols-3 gap-6">
            {[
              { icon: Shield, title: "Intelligent Detection", desc: "ML-powered risk scoring with 99.7% accuracy. Custom rules engine with real-time adaptation." },
              { icon: Zap, title: "Real-Time Processing", desc: "Sub-50ms decision latency with Kafka streaming. Process millions of transactions per minute." },
              { icon: BarChart3, title: "Advanced Analytics", desc: "Risk heatmaps, velocity tracking, device fingerprinting, and behavioral analysis dashboards." },
            ].map((feature, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.15 }}
                className="glass-card p-8 space-y-4"
              >
                <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center">
                  <feature.icon className="h-6 w-6 text-primary" />
                </div>
                <h3 className="text-xl font-semibold">{feature.title}</h3>
                <p className="text-muted-foreground text-sm leading-relaxed">{feature.desc}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section id="how" className="py-24 px-6">
        <div className="max-w-5xl mx-auto">
          <motion.h2
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            className="text-3xl md:text-4xl font-bold text-center mb-16"
          >
            How It Works
          </motion.h2>
          <div className="flex flex-col md:flex-row items-center gap-4">
            {[
              { icon: Globe, label: "Transaction" },
              { icon: Activity, label: "Kafka Stream" },
              { icon: Shield, label: "Fraud Engine" },
              { icon: Lock, label: "Decision" },
              { icon: BarChart3, label: "Dashboard" },
            ].map((step, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, scale: 0.8 }}
                whileInView={{ opacity: 1, scale: 1 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.1 }}
                className="flex items-center gap-4"
              >
                <div className="glass-card-static p-4 flex flex-col items-center gap-2 min-w-[100px]">
                  <step.icon className="h-6 w-6 text-primary" />
                  <span className="text-xs font-medium">{step.label}</span>
                </div>
                {i < 4 && <ArrowRight className="h-4 w-4 text-muted-foreground hidden md:block" />}
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-24 px-6">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="max-w-3xl mx-auto glass-card-static p-12 text-center space-y-6"
        >
          <h2 className="text-3xl md:text-4xl font-bold">
            Protect your transactions <span className="text-gradient-orange">today.</span>
          </h2>
          <p className="text-muted-foreground">
            Join hundreds of enterprises using FraudGuard to stop fraud before it happens.
          </p>
          <Link
            to="/dashboard"
            className="inline-flex items-center gap-2 bg-primary text-primary-foreground px-8 py-3.5 rounded-xl font-semibold hover:opacity-90 transition-all orange-glow text-lg"
          >
            Get Started Free <ArrowRight className="h-5 w-5" />
          </Link>
        </motion.div>
      </section>

      {/* Footer */}
      <footer className="border-t border-border/50 py-8 px-6">
        <div className="max-w-7xl mx-auto flex items-center justify-between text-sm text-muted-foreground">
          <div className="flex items-center gap-2">
            <Shield className="h-4 w-4 text-primary" />
            <span>FraudGuard</span>
          </div>
          <span>© 2026 FraudGuard. All rights reserved.</span>
        </div>
      </footer>
    </div>
  );
};

export default Landing;
