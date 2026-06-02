"use client";

import Link from "next/link";
import { BrandMark } from "./brand-mark";
import styles from "./product-shell.module.css";

type PlaceholderScreenProps = {
  badge: string;
  title: string;
  description: string;
  primaryHref: string;
  primaryLabel: string;
  secondaryHref?: string;
  secondaryLabel?: string;
};

export function PlaceholderScreen({
  badge,
  title,
  description,
  primaryHref,
  primaryLabel,
  secondaryHref,
  secondaryLabel,
}: PlaceholderScreenProps) {
  return (
    <main className={styles.productPage}>
      <div className={styles.pageGlow} aria-hidden="true" />

      <header className={styles.subpageHeader}>
        <Link href="/" className={styles.inlineLink}>
          Volver al producto
        </Link>
        <BrandMark compact />
        <span className={styles.progressHint}>Placeholder</span>
      </header>

      <section className={styles.placeholderSection}>
        <article className={styles.placeholderCard}>
          <span className={styles.sectionBadge}>{badge}</span>
          <h1 className={styles.sectionTitle}>{title}</h1>
          <p className={styles.sectionText}>{description}</p>
          <div className={styles.placeholderActions}>
            <Link href={primaryHref} className={styles.primaryButton}>
              <span>{primaryLabel}</span>
            </Link>
            {secondaryHref && secondaryLabel ? (
              <Link href={secondaryHref} className={styles.secondaryButton}>
                {secondaryLabel}
              </Link>
            ) : null}
          </div>
        </article>
      </section>
    </main>
  );
}
