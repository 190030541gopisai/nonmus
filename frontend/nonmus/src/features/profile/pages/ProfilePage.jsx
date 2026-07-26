import { FaEdit } from "react-icons/fa";
import {useEffect, useRef, useState} from "react";
import {useAuth} from "../../auth/hooks/useAuth.js";
import {useProfilePicture} from "../../auth/hooks/useProfilePicture.js";

function ProfilePage() {
  const {user} = useAuth();
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
    <div>
      <div className="relative flex items-center p-4">
        <div className="w-1/4 flex items-center justify-center">
          {editProfile ? (
            <label htmlFor="profile-picture-file-input" className="relative flex items-center justify-center cursor-pointer group">
              <img
                  src={imageSrc}
                  alt="Profile picture"
                  className="rounded-full w-24 h-24 object-cover opacity-25"
                  onError={handleViewUrlExpired}
              />
              <span className="absolute transition-opacity duration-300 opacity-100">
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
                className="relative"
            >
              <img
                  src={imageSrc}
                  alt="Profile picture"
                  className="rounded-full w-24 h-24 object-cover cursor-pointer"
                  onError={handleViewUrlExpired}
              />
            </button>
          )}
          {uploading && <p className="text-xs text-blue-500 mt-1 text-center">Uploading...</p>}
          {error && <p className="text-xs text-red-500 mt-1 text-center">{error}</p>}
        </div>
        <div className="w-3/4 p-4">
          {editProfile ? (
            <div>
              <input
                type="text"
                placeholder="Name"
                className="block font-bold text-lg"
                value={name}
                onChange={(e) => setName(e.target.value)}
                autoFocus
              />
              <input
                type="email"
                placeholder="Email"
                className="block w-full"
                value={email}
                onChange={e => setEmail(e.target.value)}
              />
            </div>
          ) : (
            <div>
              <h1 className="text-xl font-bold">{name || "Name"}</h1>
              <p className="text-gray-600">{email || "Email"}</p>
            </div>
          )}

          {editProfile && <div className="absolute">
            <button className="mt-2 bg-blue-500 text-white px-2 py-1 rounded" onClick={handleSubmit}>Save</button>
            <button className="mt-2 ml-2 bg-gray-500 text-white px-2 py-1 rounded" onClick={handleCancel}>Cancel</button>
          </div>}
        </div>
        {!editProfile &&
            <span className="absolute top-4 right-4 p-4 cursor-pointer md:right-1/4" onClick={() => setEditProfile(true)}>
              <FaEdit />
            </span>
        }
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
