/*
Copyright {2018} {Alexius Diakogiannis}

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
limitations under the License.
package com.diakogiannis.configuration.boubouconfigmanager.properties;
*/
package com.diakogiannis.configuration.boubouconfigmanager.connection.scm;

public class GitConnectionFactoryBuilder {
    private String gitUri;
    private String gitBranch;
    private String username;
    private String password;
    private String remotepath;

    public GitConnectionFactoryBuilder setGitUri(String gitUri) {
        this.gitUri = gitUri;
        return this;
    }

    public GitConnectionFactoryBuilder setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
        return this;
    }

    public GitConnectionFactoryBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public GitConnectionFactoryBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public GitConnectionFactoryBuilder setRemotepath(String remotepath) {
        this.remotepath = remotepath;
        return this;
    }

    public GitConnectionFactory build() {
        return new GitConnectionFactory(gitUri, gitBranch, username, password, remotepath);
    }
}