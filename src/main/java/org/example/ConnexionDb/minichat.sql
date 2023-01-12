-- phpMyAdmin SQL Dump
-- version 5.1.3
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : jeu. 12 jan. 2023 à 19:25
-- Version du serveur : 10.4.22-MariaDB
-- Version de PHP : 7.4.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `minichat`
--

-- --------------------------------------------------------

--
-- Structure de la table `amis`
--

CREATE TABLE `amis` (
  `utilisateur1` int(11) NOT NULL,
  `utilisateur2` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Déchargement des données de la table `amis`
--

INSERT INTO `amis` (`utilisateur1`, `utilisateur2`) VALUES
(1, 3),
(3, 1),
(3, 2),
(2, 3),
(3, 4),
(4, 3),
(1, 2),
(2, 1);

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `idUtilisateur` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `nom` varchar(20) NOT NULL,
  `prenom` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`idUtilisateur`, `username`, `nom`, `prenom`, `password`) VALUES
(1, 'abdessalam12', 'bou', 'abdessalam', '12345'),
(2, 'imad12', 'imad', 'imad', '12345'),
(3, 'yassine12', 'yassine', 'yassine', '12345'),
(4, 'mohamed12', 'mohamed', 'mohamed', '12345'),
(5, 'hafsa12', 'hafsa', 'hafsa', '12345'),
(6, 'mounir12', 'mounir', 'mounir', '12345'),
(7, 'khadija12', 'khadija', 'khadija', '12345');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `amis`
--
ALTER TABLE `amis`
  ADD KEY `utilisateur1` (`utilisateur1`),
  ADD KEY `utilisateur2` (`utilisateur2`);

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`idUtilisateur`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `idUtilisateur` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `amis`
--
ALTER TABLE `amis`
  ADD CONSTRAINT `amis_ibfk_1` FOREIGN KEY (`utilisateur1`) REFERENCES `utilisateur` (`idUtilisateur`),
  ADD CONSTRAINT `amis_ibfk_2` FOREIGN KEY (`utilisateur2`) REFERENCES `utilisateur` (`idUtilisateur`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
