export default function Notice({ type = "info", children }) {
  return <div className={`notice notice-${type}`}>{children}</div>;
}
