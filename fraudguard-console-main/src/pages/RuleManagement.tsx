import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Save, ToggleLeft, ToggleRight, Plus, Trash2 } from "lucide-react";
import { ruleService, FraudRule } from "@/services/ruleService";
import { toast } from "sonner";

const RuleManagement = () => {
  const [rules, setRules] = useState<FraudRule[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const fetchRules = async () => {
    try {
      const data = await ruleService.getRules();
      setRules(data);
    } catch (error) {
      console.error("Failed to fetch rules", error);
      toast.error("Failed to load rules");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRules();
  }, []);

  const toggle = async (id: number | undefined, index: number) => {
    if (id === undefined) return;
    const rule = rules[index];
    const updatedRule = { ...rule, enabled: !rule.enabled };

    // Optimistic update
    const updatedRules = [...rules];
    updatedRules[index] = updatedRule;
    setRules(updatedRules);

    try {
      await ruleService.updateRule(id, updatedRule);
      toast.success(`Rule ${updatedRule.enabled ? "enabled" : "disabled"}`);
    } catch (error) {
      console.error("Failed to toggle rule", error);
      toast.error("Failed to update rule");
      // Rollback
      fetchRules();
    }
  };

  const updateScore = (index: number, score: number) => {
    const updated = [...rules];
    updated[index].score = score;
    setRules(updated);
  };

  const handleSaveAll = async () => {
    setSaving(true);
    try {
      // In a real scenario, we might want a bulk update endpoint
      // For now, we update rules that might have changed their score
      const updatePromises = rules.map(rule => {
        if (rule.id) return ruleService.updateRule(rule.id, rule);
        return Promise.resolve();
      });
      await Promise.all(updatePromises);
      toast.success("All changes saved");
    } catch (error) {
      console.error("Failed to save rules", error);
      toast.error("Failed to save some changes");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="p-8 text-center">Loading rules...</div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Rule Management</h1>
          <p className="text-sm text-muted-foreground">Configure fraud detection rules and thresholds</p>
        </div>
        <button
          onClick={handleSaveAll}
          disabled={saving}
          className="flex items-center gap-2 bg-primary text-primary-foreground px-5 py-2.5 rounded-xl font-medium hover:opacity-90 transition-all orange-glow-sm disabled:opacity-50"
        >
          <Save className="h-4 w-4" />
          {saving ? "Saving..." : "Save Changes"}
        </button>
      </div>

      <div className="space-y-4">
        {rules.map((rule, i) => (
          <motion.div
            key={rule.id || i}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.06 }}
            className="glass-card-static p-6"
          >
            <div className="flex items-start justify-between mb-3">
              <div>
                <h3 className="font-semibold">{rule.name}</h3>
                <p className="text-xs text-muted-foreground">{rule.description}</p>
                <code className="text-[10px] bg-accent/30 px-1.5 py-0.5 rounded mt-2 inline-block text-primary">
                  {rule.condition}
                </code>
              </div>
              <button
                onClick={() => toggle(rule.id, i)}
                className="text-muted-foreground hover:text-foreground transition-colors"
                title={rule.enabled ? "Disable Rule" : "Enable Rule"}
              >
                {rule.enabled ? <ToggleRight className="h-6 w-6 text-primary" /> : <ToggleLeft className="h-6 w-6" />}
              </button>
            </div>

            <div className="flex items-center gap-4 mt-4">
              <div className="flex-1">
                <span className="text-[10px] uppercase text-muted-foreground mb-1 block">Risk Score Contribution</span>
                <input
                  type="range"
                  min={0}
                  max={100}
                  value={rule.score}
                  onChange={(e) => updateScore(i, Number(e.target.value))}
                  className="w-full accent-primary h-1.5"
                  disabled={!rule.enabled}
                />
              </div>
              <span className="text-sm font-mono min-w-[60px] text-right bg-accent/20 px-2 py-1 rounded">
                {rule.score} pts
              </span>
            </div>
          </motion.div>
        ))}
        {rules.length === 0 && (
          <div className="text-center py-12 text-muted-foreground border-2 border-dashed border-border rounded-2xl">
            No rules configured in the system.
          </div>
        )}
      </div>
    </div>
  );
};

export default RuleManagement;
