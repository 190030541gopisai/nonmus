import { useRef } from "react";
import { useProfilePicture } from "../hooks/useProfilePicture";

const ProfilePictureUploader = () => {
  const fileInputRef = useRef(null);
  const {
    viewUrl,
    uploading,
    uploadProgress,
    error,
    success,
    uploadProfilePicture,
    handleViewUrlExpired,
  } = useProfilePicture();

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (file) uploadProfilePicture(file);
    // Reset input so the same file can be re-selected if needed
    e.target.value = "";
  };

  return (
    <div className="flex flex-col items-center gap-4">
      {/* Avatar — clicking it opens the file picker */}
      <button
        id="profile-picture-upload-btn"
        type="button"
        onClick={() => fileInputRef.current?.click()}
        disabled={uploading}
        className="group relative h-28 w-28 cursor-pointer rounded-full border-2 border-white/20 bg-white/10 shadow-xl transition-all duration-300 hover:border-violet-400/60 hover:shadow-violet-500/20 disabled:cursor-not-allowed disabled:opacity-60"
        title="Click to change profile picture"
        aria-label="Upload profile picture"
      >
        {/* Profile image or placeholder */}
        {viewUrl ? (
          <img
            src={viewUrl}
            alt="Profile picture"
            onError={handleViewUrlExpired}
            className="h-full w-full rounded-full object-cover"
          />
        ) : (
          <span className="flex h-full w-full items-center justify-center rounded-full text-4xl text-white/40">
            {/* Person silhouette */}
            <svg viewBox="0 0 24 24" fill="currentColor" className="h-14 w-14">
              <path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z" />
            </svg>
          </span>
        )}

        {/* Hover overlay with camera icon */}
        <span className="absolute inset-0 flex items-center justify-center rounded-full bg-black/50 opacity-0 transition-opacity duration-200 group-hover:opacity-100">
          <svg viewBox="0 0 24 24" fill="white" className="h-7 w-7">
            <path d="M12 15.2A3.2 3.2 0 1 0 12 8.8a3.2 3.2 0 0 0 0 6.4z" />
            <path d="M9 2L7.17 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2h-3.17L15 2H9zm3 15a5 5 0 1 1 0-10 5 5 0 0 1 0 10z" />
          </svg>
        </span>

        {/* Uploading spinner overlay */}
        {uploading && (
          <span className="absolute inset-0 flex items-center justify-center rounded-full bg-black/60">
            <svg
              className="h-8 w-8 animate-spin text-violet-400"
              viewBox="0 0 24 24"
              fill="none"
            >
              <circle
                className="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                strokeWidth="4"
              />
              <path
                className="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8v8H4z"
              />
            </svg>
          </span>
        )}
      </button>

      {/* Hidden file input */}
      <input
        ref={fileInputRef}
        id="profile-picture-file-input"
        type="file"
        accept="image/jpeg,image/png,image/webp,image/gif"
        onChange={handleFileChange}
        className="hidden"
        aria-hidden="true"
      />

      {/* Progress bar */}
      {uploading && (
        <div className="w-full max-w-[112px] overflow-hidden rounded-full bg-white/10">
          <div
            className="h-1 rounded-full bg-gradient-to-r from-violet-500 to-fuchsia-500 transition-all duration-300"
            style={{ width: `${uploadProgress}%` }}
          />
        </div>
      )}

      {/* Status messages */}
      {error && (
        <p
          role="alert"
          className="max-w-[220px] text-center text-xs font-medium text-red-400"
        >
          {error}
        </p>
      )}
      {success && !uploading && (
        <p className="text-xs font-medium text-emerald-400">
          ✓ Profile picture updated
        </p>
      )}
      {!uploading && !error && !success && (
        <p className="text-xs text-white/40">Click to change photo</p>
      )}
    </div>
  );
};

export default ProfilePictureUploader;
