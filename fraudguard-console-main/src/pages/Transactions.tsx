import { useState, useEffect } from "react";
import { Search, Filter, ChevronDown, X } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { transactionService, Transaction } from "@/services/transactionService";

const statusCls: Record<string, string> = {
  BLOCK: "status-block",
  ALERT: "status-alert",
  ALLOW: "status-allow",
  FLAG: "status-alert"
};

const Transactions = () => {
  const [search, setSearch] = useState("");
  const [selectedTx, setSelectedTx] = useState<Transaction | null>(null);
  const [filterDecision, setFilterDecision] = useState<string>("ALL");
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchTransactions = async () => {
      setLoading(true);
      try {
        const params: any = { size: 50 };
        if (search) params.userId = search; // API only supports filtering by userId currently
        if (filterDecision !== "ALL") params.decision = filterDecision;

        const data = await transactionService.getTransactions(params);
        setTransactions(data.content || []);
      } catch (error) {
        console.error("Failed to fetch transactions", error);
        setTransactions([]);
      } finally {
        setLoading(false);
      }
    };

    // Debounce search
    const timeoutId = setTimeout(() => {
      fetchTransactions();
    }, 500);

    return () => clearTimeout(timeoutId);
  }, [search, filterDecision]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Transactions</h1>
        <p className="text-sm text-muted-foreground">Monitor and investigate all transactions</p>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <input
            type="text"
            placeholder="Search by User ID..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-accent/50 border border-border text-sm text-foreground placeholder:text-muted-foreground/50 focus:outline-none focus:ring-2 focus:ring-primary/50"
          />
        </div>
        <div className="flex gap-2">
          {["ALL", "ALLOW", "ALERT", "BLOCK"].map((d) => (
            <button
              key={d}
              onClick={() => setFilterDecision(d)}
              className={`px-3 py-2 rounded-xl text-xs font-medium transition-all border ${filterDecision === d
                ? "bg-primary/10 border-primary/30 text-primary"
                : "border-border text-muted-foreground hover:text-foreground hover:bg-accent/50"
                }`}
            >
              {d}
            </button>
          ))}
        </div>
      </div>

      {/* Table */}
      <div className="glass-card-static overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border/50">
                <th className="text-left py-3 px-4 text-xs font-medium text-muted-foreground uppercase tracking-wider">Transaction ID</th>
                <th className="text-left py-3 px-4 text-xs font-medium text-muted-foreground uppercase tracking-wider">User</th>
                <th className="text-right py-3 px-4 text-xs font-medium text-muted-foreground uppercase tracking-wider">Amount</th>
                <th className="text-center py-3 px-4 text-xs font-medium text-muted-foreground uppercase tracking-wider">Risk</th>
                <th className="text-center py-3 px-4 text-xs font-medium text-muted-foreground uppercase tracking-wider">Decision</th>
                <th className="text-right py-3 px-4 text-xs font-medium text-muted-foreground uppercase tracking-wider">Timestamp</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan={6} className="text-center py-8">Loading transactions...</td></tr>
              ) : transactions.map((tx, i) => (
                <motion.tr
                  key={tx.transactionId}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: i * 0.02 }}
                  className="border-b border-border/30 hover:bg-accent/20 transition-colors cursor-pointer"
                  onClick={() => setSelectedTx(tx)}
                >
                  <td className="py-3 px-4 font-mono text-xs">{tx.transactionId}</td>
                  <td className="py-3 px-4 font-mono text-xs text-muted-foreground">{tx.userId}</td>
                  <td className="py-3 px-4 text-right font-mono">₹{tx.amount.toLocaleString()}</td>
                  <td className="py-3 px-4 text-center">
                    <div className="flex items-center justify-center gap-2">
                      <div className="w-12 h-1.5 rounded-full bg-accent/50 overflow-hidden">
                        <div
                          className="h-full rounded-full"
                          style={{
                            width: `${tx.riskScore || 0}%`,
                            background: (tx.riskScore || 0) > 70 ? "hsl(0, 84%, 60%)" : (tx.riskScore || 0) > 40 ? "hsl(38, 92%, 50%)" : "hsl(142, 71%, 45%)",
                          }}
                        />
                      </div>
                      <span className="text-xs text-muted-foreground">{tx.riskScore || 0}</span>
                    </div>
                  </td>
                  <td className="py-3 px-4 text-center"><span className={statusCls[tx.decision] || "status-allow"}>{tx.decision}</span></td>
                  <td className="py-3 px-4 text-right text-xs text-muted-foreground">
                    {new Date(tx.createdAt).toLocaleString()}
                  </td>
                </motion.tr>
              ))}
              {!loading && transactions.length === 0 && (
                <tr><td colSpan={6} className="text-center py-8 text-muted-foreground">No transactions found</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Transaction Detail Modal */}
      <AnimatePresence>
        {selectedTx && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 flex items-center justify-center bg-background/80 backdrop-blur-sm p-6"
            onClick={() => setSelectedTx(null)}
          >
            <motion.div
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.95, opacity: 0 }}
              className="glass-card-static p-8 w-full max-w-lg space-y-6"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex items-center justify-between">
                <h2 className="text-lg font-bold">{selectedTx.transactionId}</h2>
                <button onClick={() => setSelectedTx(null)} className="text-muted-foreground hover:text-foreground">
                  <X className="h-5 w-5" />
                </button>
              </div>

              <div className="grid grid-cols-2 gap-4 text-sm">
                <div><span className="text-muted-foreground block text-xs mb-1">User</span>{selectedTx.userId}</div>
                <div><span className="text-muted-foreground block text-xs mb-1">Amount</span>₹{selectedTx.amount.toLocaleString()}</div>
                <div><span className="text-muted-foreground block text-xs mb-1">Risk Score</span>{selectedTx.riskScore || 0}/100</div>
                <div><span className="text-muted-foreground block text-xs mb-1">Decision</span><span className={statusCls[selectedTx.decision] || "status-allow"}>{selectedTx.decision}</span></div>
              </div>

              <div>
                <h4 className="text-xs font-semibold text-muted-foreground mb-3 uppercase tracking-wider">Risk Factors</h4>
                <div className="space-y-2">
                  {selectedTx.reason ? (
                    selectedTx.reason.split(", ").map((factor, i) => (
                      <div key={i} className="flex items-center justify-between p-2.5 rounded-lg bg-accent/20 text-sm">
                        <span>{factor}</span>
                        <span className="text-destructive font-mono text-xs">Triggered</span>
                      </div>
                    ))
                  ) : (
                    <div className="flex items-center justify-between p-2.5 rounded-lg bg-accent/20 text-sm">
                      <span>No specific risk factors flagged.</span>
                      <span className="text-success font-mono text-xs">Clean</span>
                    </div>
                  )}
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default Transactions;
