const API_BASE_URL = 'http://localhost:8080/api';

const getToken = () => localStorage.getItem('accessToken');

const ApiClient = {
    async request(endpoint, options = {}) {
        const token = getToken();

        if (token) {
            options.headers = {
                ...options.headers,
                'Authorization': `Bearer ${token}`,
            };
        }

        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
            console.log(`API request to ${API_BASE_URL}${endpoint} returned status ${response.status}`);

            // If response status is 204, return null (no content)
            if (response.status === 204) {
                return null;
            }

            if (!response.ok) {
                const errorText = await response.text();
                console.error('API request failed:', errorText);
                throw new Error(`API request failed: ${response.status} ${errorText}`);
            }

            // Check the content type before parsing the response
            const contentType = response.headers.get('Content-Type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            } else {
                return await response.text();
            }
        } catch (error) {
            console.error('An error occurred during the API request:', error);
            throw error;  // Re-throw the error after logging
        }
    },

    async get(endpoint) {
        return this.request(endpoint, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        });
    },

    async post(endpoint, body) {
        return this.request(endpoint, {
            method: 'POST',
            headers: body instanceof FormData ? {} : { 'Content-Type': 'application/json' },
            body: body instanceof FormData ? body : JSON.stringify(body)
        });
    },

    async put(endpoint, body) {
        return this.request(endpoint, {
            method: 'PUT',
            headers: body instanceof FormData ? {} : { 'Content-Type': 'application/json' },
            body: body instanceof FormData ? body : JSON.stringify(body)
        });
    },


    async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    },

    // Method for uploading a file using multipart/form-data
    async postMultipartFile(endpoint, file, additionalData = {}) {
        const formData = new FormData();

        // Append the file to the FormData object
        formData.append('file', file);

        // Append additional form data if provided
        for (const key in additionalData) {
            if (additionalData.hasOwnProperty(key)) {
                formData.append(key, additionalData[key]);
            }
        }

        return this.request(endpoint, {
            method: 'POST',
            body: formData,
            // Do not set Content-Type header; fetch will automatically set it to multipart/form-data
        });
    },
};

export default ApiClient;
