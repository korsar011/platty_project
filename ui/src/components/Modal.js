import React from 'react';

const Modal = ({ isOpen, closeModal, children }) => {
    if (!isOpen) return null;

    const closeModalOnClick = (e) => {
        if (e.target.className === 'modal') closeModal();
    };

    return (
        <div className="modal" onClick={closeModalOnClick} style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <div className="modal-content">
                {children}
            </div>
        </div>
    );
};

export default Modal;
