import type { SVGProps } from "react";

type IconProps = SVGProps<SVGSVGElement> & {
  title?: string;
};

function IconBase({ children, title, ...props }: IconProps) {
  return (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden={title ? undefined : true}
      role={title ? "img" : undefined}
      {...props}
    >
      {title ? <title>{title}</title> : null}
      {children}
    </svg>
  );
}

export function LeafIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <path d="M5 14c0-6 6-10 14-10 0 8-4 14-10 14-2.2 0-4-.8-5.4-2.2" />
      <path d="M5 14c0 2.8 1 4.6 3 6" />
      <path d="M8 16c2-4 5.5-7 10-9" />
    </IconBase>
  );
}

export function UtensilsIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <path d="M4 3v7a3 3 0 0 0 3 3v8" />
      <path d="M7 3v10" />
      <path d="M10 3v7" />
      <path d="M17 3c-2 2-3 4.6-3 8v10" />
      <path d="M17 3v18" />
    </IconBase>
  );
}

export function SparkIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <path d="m12 3 1.8 4.7L18 9.5l-4.2 1.8L12 16l-1.8-4.7L6 9.5l4.2-1.8z" />
      <path d="M19 15.5 20 18l2.5 1-2.5 1L19 22l-1-2.5-2.5-1 2.5-1z" />
      <path d="M5 16 6 18.4 8.4 19 6 20l-1 2.4L4 20l-2.4-1L4 18.4z" />
    </IconBase>
  );
}

export function BarChartIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <path d="M4 20V10" />
      <path d="M10 20V4" />
      <path d="M16 20v-7" />
      <path d="M22 20v-11" />
    </IconBase>
  );
}

export function DumbbellIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <path d="M3 10v4" />
      <path d="M6 8v8" />
      <path d="M9 9h6" />
      <path d="M15 8v8" />
      <path d="M18 10v4" />
      <path d="M21 9v6" />
    </IconBase>
  );
}

export function MenuIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <path d="M4 7h16" />
      <path d="M4 12h16" />
      <path d="M4 17h16" />
    </IconBase>
  );
}

export function CloseIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <path d="m6 6 12 12" />
      <path d="m18 6-12 12" />
    </IconBase>
  );
}

export function ArrowRightIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <path d="M5 12h14" />
      <path d="m13 6 6 6-6 6" />
    </IconBase>
  );
}

export function UserCircleIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <circle cx="12" cy="8" r="4" />
      <path d="M5.5 19a8.5 8.5 0 0 1 13 0" />
    </IconBase>
  );
}

export function ChevronLeftIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <path d="m15 18-6-6 6-6" />
    </IconBase>
  );
}

export function ShieldIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <path d="M12 3 5 6v5c0 5 3.4 8.7 7 10 3.6-1.3 7-5 7-10V6z" />
    </IconBase>
  );
}

export function ClockIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <circle cx="12" cy="12" r="9" />
      <path d="M12 7v5l3 2" />
    </IconBase>
  );
}

export function PhoneIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <rect x="7" y="2.5" width="10" height="19" rx="2.5" />
      <path d="M11 18h2" />
    </IconBase>
  );
}

export function GlobeIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <circle cx="12" cy="12" r="9" />
      <path d="M3 12h18" />
      <path d="M12 3a15 15 0 0 1 0 18" />
      <path d="M12 3a15 15 0 0 0 0 18" />
    </IconBase>
  );
}

export function CheckCircleIcon(props: IconProps) {
  return (
    <IconBase {...props}>
      <circle cx="12" cy="12" r="9" />
      <path d="m8.5 12 2.3 2.3 4.7-5" />
    </IconBase>
  );
}
