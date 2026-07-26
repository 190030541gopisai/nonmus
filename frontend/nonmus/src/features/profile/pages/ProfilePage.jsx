import { FaEdit } from "react-icons/fa";
import { TbLogout } from "react-icons/tb";
import {useEffect, useRef, useState} from "react";
import {useAuth} from "../../auth/hooks/useAuth.js";
import {useProfilePicture} from "../../auth/hooks/useProfilePicture.js";

function ProfilePage() {
  const {user, logout} = useAuth();
  const {
    viewUrl,
    uploading,
    error,
    loadViewUrl,
    uploadProfilePicture,
    handleViewUrlExpired,
  } = useProfilePicture();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [profilePictureFile, setProfilePictureFile] = useState(null);
  const [previewProfilePicture, setPreviewProfilePicture] = useState("");
  const [editProfile, setEditProfile] = useState(false);
  const [showPreview, setShowPreview] = useState(false);
  const objectUrlRef = useRef(null);

  const expiredUrl = `https://ui-avatars.com/api/?name=${name || "User"}&background=e5e7eb&color=6b7280&size=256`;

  useEffect(() => {
    loadViewUrl();
  }, [loadViewUrl]);

  useEffect(() => {
    if (user) {
      setName(user.name || "");
      setEmail(user.email || "");
    }
  }, [user]);

  useEffect(() => {
    if (!editProfile) {
      setPreviewProfilePicture(viewUrl);
    }
  }, [editProfile, viewUrl]);

  const revokeObjectUrl = () => {
    if (objectUrlRef.current) {
      URL.revokeObjectURL(objectUrlRef.current);
      objectUrlRef.current = null;
    }
  };

  const handleSubmit = () => {
    if (profilePictureFile) {
      uploadProfilePicture(profilePictureFile);
      setProfilePictureFile(null);
      revokeObjectUrl();
    }
    setEditProfile(false);
  };

  const handleCancel = () => {
    if (user) {
      setName(user.name || "");
      setEmail(user.email || "");
    }
    setProfilePictureFile(null);
    revokeObjectUrl();
    setPreviewProfilePicture(viewUrl);
    setEditProfile(false);
  };

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      revokeObjectUrl();
      const objectUrl = URL.createObjectURL(file);
      objectUrlRef.current = objectUrl;
      setProfilePictureFile(file);
      setPreviewProfilePicture(objectUrl);
    }
  };

  const imageSrc = editProfile && previewProfilePicture
    ? previewProfilePicture
    : (viewUrl || expiredUrl);

  return (
    <div className="mx-auto max-w-2xl px-4 py-6 lg:py-10">
      <div className="relative rounded-2xl border border-slate-200 bg-white p-4 lg:p-8">
        <div className="flex flex-col items-center gap-6 lg:flex-row lg:items-start">
          <div className="flex-shrink-0">
            {editProfile ? (
              <label htmlFor="profile-picture-file-input" className="relative mx-auto flex h-24 w-24 cursor-pointer items-center justify-center rounded-full group lg:h-28 lg:w-28">
                <img
                    src={imageSrc}
                    alt="Profile picture"
                    className="h-full w-full rounded-full object-cover opacity-25"
                    onError={handleViewUrlExpired}
                />
                <span className="absolute opacity-100">
                  <svg viewBox="0 0 24 24" className="h-7 w-7">
                    <path d="M12 15.2A3.2 3.2 0 1 0 12 8.8a3.2 3.2 0 0 0 0 6.4z" />
                    <path d="M9 2L7.17 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2h-3.17L15 2H9zm3 15a5 5 0 1 1 0-10 5 5 0 0 1 0 10z" />
                  </svg>
                </span>
                <input
                    id="profile-picture-file-input"
                    type="file"
                    accept="image/jpeg,image/png,image/webp,image/gif"
                    className="hidden"
                    aria-hidden="true"
                    onChange={handleFileChange}
                />
              </label>
            ) : (
              <button
                  type="button"
                  onClick={() => setShowPreview(true)}
                  className="mx-auto block"
              >
                <img
                    src={imageSrc}
                    alt="Profile picture"
                    className="h-24 w-24 rounded-full object-cover cursor-pointer lg:h-28 lg:w-28"
                    onError={handleViewUrlExpired}
                />
              </button>
            )}
            {uploading && <p className="mt-2 text-center text-xs text-blue-500">Uploading...</p>}
            {error && <p className="mt-2 text-center text-xs text-red-500">{error}</p>}
          </div>

          <div className="flex-1 text-center lg:text-left">
            {editProfile ? (
              <div className="space-y-3">
                <input
                  type="text"
                  placeholder="Name"
                  className="block w-full rounded-lg border border-slate-300 px-3 py-2 text-lg font-bold focus:border-slate-500 focus:outline-none"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  autoFocus
                />
                <input
                  type="email"
                  placeholder="Email"
                  className="block w-full rounded-lg border border-slate-300 px-3 py-2 focus:border-slate-500 focus:outline-none"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                />
              </div>
            ) : (
              <div>
                <h1 className="text-xl font-bold lg:text-2xl">{name || "Name"}</h1>
                <p className="text-gray-600">{email || "Email"}</p>
              </div>
            )}

            {editProfile && (
              <div className="mt-4 flex justify-center gap-3 lg:justify-start">
                <button className="rounded-lg bg-blue-500 px-4 py-2 text-sm font-medium text-white hover:bg-blue-600" onClick={handleSubmit}>Save</button>
                <button className="rounded-lg bg-gray-500 px-4 py-2 text-sm font-medium text-white hover:bg-gray-600" onClick={handleCancel}>Cancel</button>
              </div>
            )}
            {!editProfile && (
              <button
                  className="mt-4 inline-flex items-center gap-2 text-sm font-medium text-slate-500 hover:text-slate-700 lg:mt-4"
                  onClick={() => setEditProfile(true)}
              >
                <FaEdit /> Edit Profile
              </button>
            )}
          </div>
        </div>
        <div className="mt-6 border-t border-slate-200 pt-4 lg:hidden">
          <button
              type="button"
              onClick={logout}
              className="flex w-full items-center justify-center gap-2 rounded-lg px-3 py-2.5 text-sm font-medium text-slate-500 transition hover:bg-red-50 hover:text-red-600"
          >
            <TbLogout size={18} />
            Logout
          </button>
        </div>
      </div>

      {showPreview && (
        <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/80"
            onClick={() => setShowPreview(false)}
        >
          <button
              type="button"
              className="absolute top-4 right-4 text-white text-3xl hover:text-gray-300 z-10"
              onClick={() => setShowPreview(false)}
          >
            &times;
          </button>
          <img
              src={imageSrc}
              alt="Profile picture"
              className="max-w-[90vw] max-h-[90vh] rounded-lg object-contain"
              onClick={(e) => e.stopPropagation()}
          />
        </div>
      )}
    </div>
  );
}

export default ProfilePage;
