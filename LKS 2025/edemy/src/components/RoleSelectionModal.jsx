import React, { useState } from 'react';
import { SignUpButton } from '@clerk/clerk-react';
import { GraduationCap, User, X } from 'lucide-react';

const RoleSelectionModal = ({ isOpen, onClose }) => {
  const [selectedRole, setSelectedRole] = useState(null);

  if (!isOpen) return null;

  const roles = [
    {
      id: 'student',
      title: 'Student',
      description: 'Learn from expert instructors and advance your skills',
      icon: User,
      color: 'blue',
      features: [
        'Access to all courses',
        'Track your progress',
        'Get certificates',
        'Join community discussions'
      ]
    },
    {
      id: 'educator',
      title: 'Educator',
      description: 'Share your knowledge and teach students worldwide',
      icon: GraduationCap,
      color: 'green',
      features: [
        'Create and publish courses',
        'Manage your students',
        'Earn money from teaching',
        'Analytics and insights'
      ]
    }
  ];

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-4xl w-full mx-4 max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Choose Your Role</h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-full transition-colors"
          >
            <X className="h-5 w-5 text-gray-500" />
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {roles.map((role) => {
            const Icon = role.icon;
            const isSelected = selectedRole === role.id;
            
            return (
              <div
                key={role.id}
                onClick={() => setSelectedRole(role.id)}
                className={`cursor-pointer border-2 rounded-lg p-6 transition-all hover:shadow-md ${
                  isSelected
                    ? `border-${role.color}-500 bg-${role.color}-50`
                    : 'border-gray-200 hover:border-gray-300'
                }`}
              >
                <div className="flex items-center mb-4">
                  <div className={`p-3 rounded-full bg-${role.color}-100`}>
                    <Icon className={`h-6 w-6 text-${role.color}-600`} />
                  </div>
                  <div className="ml-4">
                    <h3 className="text-xl font-semibold text-gray-900">{role.title}</h3>
                    <p className="text-gray-600">{role.description}</p>
                  </div>
                </div>

                <ul className="space-y-2">
                  {role.features.map((feature, index) => (
                    <li key={index} className="flex items-center text-sm text-gray-600">
                      <div className={`w-2 h-2 rounded-full bg-${role.color}-500 mr-3`}></div>
                      {feature}
                    </li>
                  ))}
                </ul>

                {isSelected && (
                  <div className="mt-4">
                    <SignUpButton
                      mode="modal"
                      redirectUrl={role.id === 'educator' ? '/educator/dashboard' : '/my-courses'}
                      signUpForceRedirectUrl={role.id === 'educator' ? '/educator/dashboard' : '/my-courses'}
                      unsafeMetadata={{ role: role.id }}
                    >
                      <button className={`w-full bg-${role.color}-600 text-white py-3 px-4 rounded-lg font-medium hover:bg-${role.color}-700 transition-colors`}>
                        Sign Up as {role.title}
                      </button>
                    </SignUpButton>
                  </div>
                )}
              </div>
            );
          })}
        </div>

        {!selectedRole && (
          <div className="text-center mt-6">
            <p className="text-gray-600">Select a role to continue with sign up</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default RoleSelectionModal;
