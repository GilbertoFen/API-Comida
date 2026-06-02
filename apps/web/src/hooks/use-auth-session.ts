"use client";

import { useEffect, useState } from "react";
import { clearStoredSession, getStoredSession } from "@/services/api-client";
import type { AuthSession } from "@/types/domain";

export function useAuthSession() {
  const [session, setSession] = useState<AuthSession | null>(null);
  const [isHydrated, setIsHydrated] = useState(false);

  useEffect(() => {
    setSession(getStoredSession());
    setIsHydrated(true);
  }, []);

  function clearSession() {
    clearStoredSession();
    setSession(null);
  }

  return {
    session,
    setSession,
    clearSession,
    isHydrated,
  };
}
