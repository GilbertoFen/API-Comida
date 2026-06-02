import styles from "./product-shell.module.css";
import { LeafIcon } from "./icons";

export function BrandMark({ compact = false }: { compact?: boolean }) {
  return (
    <div className={compact ? styles.brandCompact : styles.brandLockup}>
      <div className={styles.brandOrb} aria-hidden="true">
        <div className={styles.brandRing} />
        <div className={styles.brandPulse} />
        <LeafIcon className={styles.brandIcon} />
      </div>
      <div>
        <strong className={styles.brandName}>AppFoodSpring</strong>
        <span className={styles.brandTag}>Health tracker · self quantification</span>
      </div>
    </div>
  );
}
