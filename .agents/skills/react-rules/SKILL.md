---
name: react-rules
description: Generate and modify React applications, components, hooks, global state, forms, and UI logic using React with TypeScript. Use when the user asks to create a React app or component, add or modify React components, implement hooks, Zustand state, Zod validation, React Hook Form forms, API data fetching, or React UI behavior.
---

# React Rules

## Core Workflow

Use TypeScript for every React project and React file. Prefer `.tsx` for components and `.ts` for stores, schemas, hooks, and utilities.

Treat `/src` as the primary source for React application code. Start from the existing `/src` structure before creating new folders, and keep generated output such as `dist/` out of manual edits.

When creating a new project, use React `19.2.4` or newer. Prefer the latest stable npm `react` version available at implementation time, and verify it with npm before pinning if network access is available:

```bash
npm view react version
```

Create a conventional React + TypeScript structure:

```text
src/
  app/
  components/
  hooks/
  schemas/
  stores/
  services/
  utils/
  types/
```

Keep components small, simple, and focused on one responsibility. Extract reusable behavior into custom hooks such as `useAuth`, `useFetch`, or domain-specific hooks.

## State

Use Zustand for global client state. Create stores with `create()` and define state plus actions in the store.

```ts
import { create } from "zustand";

type CounterState = {
  count: number;
  increment: () => void;
  reset: () => void;
};

export const useCounterStore = create<CounterState>((set) => ({
  count: 0,
  increment: () => set((state) => ({ count: state.count + 1 })),
  reset: () => set({ count: 0 }),
}));
```

Never mutate React or Zustand state directly. Always return new object or array copies when updating nested or collection state.

## Validation And Forms

Use Zod for runtime data validation. Define schemas with `z.object`, `z.string`, and related Zod primitives. Validate input with `parse` when exceptions are acceptable, and `safeParse` when the UI or caller should handle validation errors.

```ts
import { z } from "zod";

export const userSchema = z.object({
  name: z.string().min(1),
  email: z.string().email(),
});

export type User = z.infer<typeof userSchema>;
```

For React forms, integrate Zod with React Hook Form using a resolver, normally `zodResolver`.

Keep schema definitions outside components unless the schema truly depends on runtime component data.

## Effects And Render Logic

Use `useEffect` only to synchronize with external systems such as APIs, DOM APIs, subscriptions, timers, browser storage, or third-party libraries.

Do not use `useEffect` for logic that can be derived from props or state. Calculate derived values during render, in event handlers, or with `useMemo` for expensive computations.

Keep effects small and give them clear dependency arrays. Clean up subscriptions, timers, and external listeners.

Do not put user-interaction logic in effects. Put logic caused by clicks, form changes, submissions, and other user actions in event handlers.

Avoid side effects during render. Components and hooks must stay pure.

## Hooks And Composition

Never call hooks inside loops, conditionals, or nested functions. Call hooks only at the top level of React components or custom hooks.

Share logic between events with reusable functions or custom hooks.

Use callbacks passed through props to communicate changes from child components to parent components. Execute those callbacks from the child when the relevant event or state change happens.

Use `key`, derived state, or event-based state updates to reset or adjust local state. Avoid effect-based state resets unless an external system requires synchronization.

## Data Fetching

Use React Query or SWR when a component needs API data that should be cached, shared, refetched, deduplicated, or reused across components.

Keep raw API calls in `services/` or another local convention already present in the repository. Keep components focused on rendering and user interaction.

Validate API payloads with Zod at the boundary when the shape is important for UI correctness.

## Existing Codebases

Follow the repository's existing framework, file layout, lint rules, styling system, and test conventions. Introduce Zustand, Zod, React Hook Form, React Query, or SWR only when the task needs them or when the user explicitly requests them.

For component changes, keep edits scoped to the requested behavior and add focused tests when the project has a test setup.
