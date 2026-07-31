import {useCallback, useState} from "react";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {confirmUploadApi, getProfilePictureViewUrlApi, requestPresignedUploadUrlApi, uploadFileToS3Api} from "../api/storageApi";

const ALLOWED_TYPES = ["image/jpeg", "image/png", "image/webp", "image/gif"];
const MAX_BYTES = 5 * 1024 * 1024; // 5 MB

export const useProfilePicture = () => {
  const queryClient = useQueryClient();
  const [uploadProgress, setUploadProgress] = useState(0);

  const { data: viewUrl = "", refetch: loadViewUrl } = useQuery({
    queryKey: ["profilePictureViewUrl"],
    queryFn: async () => {
      try {
        const data = await getProfilePictureViewUrlApi();
        return data.viewUrl || "";
      } catch {
        return "";
      }
    },
    staleTime: 4 * 60 * 1000,
    retry: false,
  });

  const uploadMutation = useMutation({
    mutationFn: async (file) => {
      if (!ALLOWED_TYPES.includes(file.type)) {
        throw new Error("File type not allowed. Use JPEG, PNG, WebP, or GIF.");
      }
      if (file.size > MAX_BYTES) {
        throw new Error("File is too large. Maximum size is 5 MB.");
      }

      const attemptUpload = async () => {
        const { presignedUrl, s3Key } = await requestPresignedUploadUrlApi(file.type, file.size);
        setUploadProgress(30);
        await uploadFileToS3Api(presignedUrl, file);
        setUploadProgress(70);
        const { viewUrl: freshUrl } = await confirmUploadApi(s3Key);
        setUploadProgress(100);
        return freshUrl;
      };

      try {
        return await attemptUpload();
      } catch (err) {
        if (err?.response?.status === 403) {
          return await attemptUpload();
        }
        throw err;
      }
    },
    onMutate: () => {
      setUploadProgress(10);
    },
    onSuccess: (freshUrl) => {
      // queryClient.setQueryData(["profilePictureViewUrl"], freshUrl);
      queryClient.invalidateQueries(["profilePictureViewUrl"])
    },
    onSettled: () => {
      setTimeout(() => setUploadProgress(0), 3000);
    },
  });

  const handleViewUrlExpired = useCallback(() => {
    loadViewUrl();
  }, [loadViewUrl]);

  return {
    viewUrl,
    uploading: uploadMutation.isPending,
    uploadProgress,
    error: uploadMutation.error?.response?.data?.message || uploadMutation.error?.message || null,
    success: uploadMutation.isSuccess,
    loadViewUrl,
    uploadProfilePicture: uploadMutation.mutateAsync,
    handleViewUrlExpired,
  };
};
