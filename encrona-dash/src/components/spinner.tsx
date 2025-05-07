export default function Spinner() {
  return (
    <div className="flex items-center justify-center">
      <div
        className="h-14 w-14 animate-spin rounded-full border-4 border-solid"
        style={{
          borderColor: '#00c950',
          borderTopColor: 'transparent',
        }}
      />
    </div>
  );
}
