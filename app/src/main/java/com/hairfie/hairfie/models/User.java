package com.hairfie.hairfie.models;

import android.support.annotation.NonNull;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.remoting.Repository;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

/**
 * Created by stephh on 24/11/15.
 */
public class User extends Model {
    public static class UserRepository extends ModelRepository<User> {
        public UserRepository() {
            super("users", User.class);
        }
        @Override
        public RestContract createContract() {
            RestContract contract = super.createContract();

            // Add items to the contract here.
            // For instance:
            // contract.addItem(new RestContractItem("/users/:userId/favorite-business-members/:businessMemberId", "PUT"), "users.favoriteBusinessMember");

            return contract;
        }
    }

    private static UserRepository sRepository;

    public static void initialize(@NonNull RestAdapter adapter) {
        if (null == sRepository) {
            sRepository = adapter.createRepository(UserRepository.class);
        }
    }

    @NonNull
    public static UserRepository getRepositoryInstance() {
        return sRepository;
    }
}
